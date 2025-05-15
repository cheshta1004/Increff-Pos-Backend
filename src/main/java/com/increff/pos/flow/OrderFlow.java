package com.increff.pos.flow;

import com.increff.pos.api.InventoryApi;
import com.increff.pos.api.ProductApi;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class OrderFlow {

    @Autowired
    private ProductApi productApi;

    @Autowired
    private InventoryApi inventoryApi;


    public void reduceInventory(Integer productId, Integer quantity) throws ApiException {
        inventoryApi.reduceInventory(productId, quantity);
    }

    public void restoreInventory(Integer productId, Integer quantity) throws ApiException {
        InventoryPojo inventory = getInventoryByProductId(productId);
        inventoryApi.updateInventory(productId, inventory.getQuantity() + quantity);
    }

    public ProductPojo getProductByBarcode(String barcode) throws ApiException {
        return productApi.getByBarcode(barcode);
    }

    public ProductPojo getProductById(Integer productId) throws ApiException {
        return productApi.get(productId);
    }

    public InventoryPojo getInventoryByProductId(Integer productId) throws ApiException {
        return inventoryApi.getByProductId(productId);
    }
}
