package com.increff.pos.api;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.InventoryPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class InventoryApi {

    @Autowired
    private InventoryDao inventoryDao;
// todo generic nonNull check
    public void addInventory(InventoryPojo pojo) throws ApiException {
        InventoryPojo existing = inventoryDao.select("productId", pojo.getProductId());
        if (Objects.nonNull(existing)) {
            existing.setQuantity(pojo.getQuantity());
        } else {
            inventoryDao.insert(pojo);
        }
    }

    public List<InventoryPojo> getAll() {
        return inventoryDao.selectAll();
    }

   
    public InventoryPojo getByProductId(Integer productId) throws ApiException {
        InventoryPojo pojo = inventoryDao.select("productId", productId);
        if (Objects.isNull(pojo)) {
            pojo = new InventoryPojo();
            pojo.setProductId(productId);
            pojo.setQuantity(0);
        }
        return pojo;
    }

    public void updateInventory(Integer productId, Integer newQuantity) throws ApiException {
        InventoryPojo inventory = inventoryDao.select("productId",productId);
        if (Objects.isNull(inventory)) {
            throw new ApiException("Inventory not found for product ID: " + productId);
        }
        if (newQuantity < 0) {
            throw new ApiException("Quantity must be non-negative.");
        }
        inventory.setQuantity(newQuantity);
    }

    public void reduceInventory(Integer productId, Integer quantityToReduce) throws ApiException{
        InventoryPojo existing = inventoryDao.select("productId",productId);
        if (Objects.isNull(existing)) {
            throw new ApiException("Inventory not found for productId: " + productId);
        }
        Integer currentQuantity = existing.getQuantity();
        if (currentQuantity < quantityToReduce) {
            throw new ApiException("Not enough inventory to reduce. Available: " + currentQuantity + ", requested: " + quantityToReduce);
        }
        existing.setQuantity(currentQuantity - quantityToReduce);
    }

}
