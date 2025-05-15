package com.increff.pos.api;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.util.ValidationUtil;
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
        return inventoryDao.select("productId", productId);
    }

    public void updateInventory(Integer productId, Integer newQuantity) throws ApiException {
        InventoryPojo inventory = inventoryDao.select("productId",productId);
        ValidationUtil.checkNull(inventory, "Inventory not found for productId: " + productId);
        if (newQuantity < 0) {
            throw new ApiException("Quantity must be non-negative.");
        }
        inventory.setQuantity(newQuantity);
    }

    public void reduceInventory(Integer productId, Integer quantityToReduce) throws ApiException{
        InventoryPojo existing = inventoryDao.select("productId",productId);
        ValidationUtil.checkNull(existing, "Inventory not found for productId: " + productId);
        Integer currentQuantity = existing.getQuantity();
        if (currentQuantity < quantityToReduce) {
            throw new ApiException("Not enough inventory to reduce. Available: " + currentQuantity + ", requested: " + quantityToReduce);
        }
        existing.setQuantity(currentQuantity - quantityToReduce);
    }

}
