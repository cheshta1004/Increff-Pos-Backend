package com.increff.pos.api;

import com.increff.pos.dao.ProductDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.util.ValidationUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.ArrayList;

@Service
@Transactional
public class ProductApi {
    @Autowired
    private ProductDao productDao;

    public void add(ProductPojo productPojo) throws ApiException {
        ProductPojo existing = productDao.select("barcode", productPojo.getBarcode());
        ValidationUtil.checkNonNull(existing, "Product with barcode '" + productPojo.getBarcode()
                + "' already exists.");
        productDao.insert(productPojo);
    }

    public ProductPojo get(Integer id) throws ApiException {
        ProductPojo product = productDao.select(id);
        ValidationUtil.checkNull(product, "Product with ID " + id + " does not exist.");
        return product;
    }

    public List<ProductPojo> getAll(int page, int size) {
        return productDao.selectAll(page, size);
    }

    public List<ProductPojo> getByClientName(String clientName, int page, int size) throws ApiException {
        if (productDao.getTotalCountByClientName(clientName) == 0) {
            return new ArrayList<>();
        }
        return productDao.selectByClientName(clientName, page, size);
    }

    public List<ProductPojo> getByPartialBarcode(String barcode, int page, int size) throws ApiException {
        return productDao.selectByPartialBarcode(barcode, page, size);
    }

    public Long getTotalCount() {
        return productDao.getTotalCount();
    }

    public Long getTotalCountByClientName(String clientName) throws ApiException {
        return productDao.getTotalCountByClientName(clientName);
    }

    public Long getTotalCountByPartialBarcode(String barcode) throws ApiException {
        return productDao.getTotalCountByPartialBarcode(barcode);
    }

    public ProductPojo getByBarcode(String barcode) throws ApiException {
        ProductPojo product = productDao.select("barcode", barcode);
        ValidationUtil.checkNull(product, "Product not found for barcode: " + barcode);
        return product;
    }

    public void updateByBarcode(String barcode, String newName, Double newMrp, String newImageUrl) throws ApiException {
        ProductPojo product = getByBarcode(barcode);
        product.setName(newName);
        product.setMrp(newMrp);
        product.setImageUrl(newImageUrl);
    }

}

