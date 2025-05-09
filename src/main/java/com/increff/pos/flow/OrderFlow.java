package com.increff.pos.flow;

import com.increff.pos.api.InventoryApi;
import com.increff.pos.api.ProductApi;
import com.increff.pos.dto.DtoHelper;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class OrderFlow {

    @Autowired
    private ProductApi productApi;

    @Autowired
    private InventoryApi inventoryApi;

    public ProductPojo getProductByBarcode(String barcode) throws ApiException {
        return productApi.getByBarcode(barcode);
    }

    public ProductPojo getProductById(Integer productId) throws ApiException {
        return productApi.get(productId);
    }

    public InventoryPojo getInventoryByProductId(Integer productId) throws ApiException {
        return inventoryApi.getByProductId(productId);
    }

    public void reduceInventory(Integer productId, Integer quantity) throws ApiException {
        inventoryApi.reduceInventory(productId, quantity);
    }

    public OrderData convertOrderPojoToData(OrderPojo pojo, List<OrderItemPojo> itemPojoList) throws ApiException {
        ProductPojo product = null;
        if (!itemPojoList.isEmpty()) {
            product = getProductById(itemPojoList.get(0).getProductId());
        }
        return DtoHelper.convertOrderPojoToData(pojo, itemPojoList, product);
    }
}
