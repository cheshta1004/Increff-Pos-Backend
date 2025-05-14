package com.increff.pos.api;

import com.increff.pos.dao.ProductDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.ProductPojo;
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
        if (!Objects.isNull(existing)) {
            throw new ApiException("Product with barcode '" + productPojo.getBarcode() + "' already exists.");
        }
        productDao.insert(productPojo);
    }

    public ProductPojo get(Integer id) throws ApiException {
        ProductPojo product = productDao.select(id);
        if (Objects.isNull(product)) {
            throw new ApiException("Product with ID " + id + " does not exist.");
        }
        return product;
    }

    public List<ProductPojo> getAll(int page, int size) {
        return productDao.selectAll(page, size);
    }

    public List<ProductPojo> getByClientName(String clientName, int page, int size) throws ApiException {
        String normalizedClientName = clientName.trim().toLowerCase();
        if (Objects.isNull(normalizedClientName) || normalizedClientName.isEmpty()) {
            throw new ApiException("Client name cannot be empty");
        }
        if (productDao.getTotalCountByClientName(normalizedClientName) == 0) {
            return new ArrayList<>();
        }
        return productDao.selectByClientName(normalizedClientName, page, size);
    }

    public List<ProductPojo> getByPartialBarcode(String barcode, int page, int size) throws ApiException {
        if (Objects.isNull(barcode) || barcode.trim().isEmpty()) {
            throw new ApiException("Barcode cannot be empty");
        }
        return productDao.selectByPartialBarcode(barcode.trim(), page, size);
    }

    public Long getTotalCount() {
        return productDao.getTotalCount();
    }

    public Long getTotalCountByClientName(String clientName) throws ApiException {
        if (Objects.isNull(clientName) || clientName.trim().isEmpty()) {
            throw new ApiException("Client name cannot be empty");
        }
        return productDao.getTotalCountByClientName(clientName.trim());
    }

    public Long getTotalCountByPartialBarcode(String barcode) throws ApiException {
        if (Objects.isNull(barcode) || barcode.trim().isEmpty()) {
            throw new ApiException("Barcode cannot be empty");
        }
        return productDao.getTotalCountByPartialBarcode(barcode.trim());
    }

    public ProductPojo getByBarcode(String barcode) throws ApiException {
        String normalizedBarcode = barcode.trim().toLowerCase();
        ProductPojo product = productDao.select("barcode", normalizedBarcode);
        if (Objects.isNull(product)) {
            throw new ApiException("Product not found for barcode: " + normalizedBarcode);
        }
        return product;
    }

    public void updateByBarcode(String barcode, String newName, Double newMrp, String newImageUrl) throws ApiException {
        if (Objects.isNull(barcode) || barcode.trim().isEmpty()) {
            throw new ApiException("Barcode cannot be empty");
        }
        ProductPojo product = getByBarcode(barcode);
        product.setName(newName);
        product.setMrp(newMrp);
        product.setImageUrl(newImageUrl);
        productDao.update(product);
    }

    public List<ProductPojo> searchProducts(String searchTerm, int page, int size) throws ApiException {
        return productDao.searchProducts(searchTerm, page, size);
    }

    public long getTotalSearchResults(String searchTerm) throws ApiException {
        return productDao.getTotalSearchResults(searchTerm);
    }
}

