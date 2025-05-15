package com.increff.pos.flow;

import com.increff.pos.api.ProductApi;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InventoryFlow {
    @Autowired
    private ProductApi productApi;

    public ProductPojo getProductByBarcode(String barcode) throws ApiException {
        return productApi.getByBarcode(barcode);
    }
    public Integer getProductIdFromForm(InventoryForm form) throws ApiException{
        ProductPojo product = getProductByBarcode(form.getProductBarcode());
        return product.getId();
    }
}
