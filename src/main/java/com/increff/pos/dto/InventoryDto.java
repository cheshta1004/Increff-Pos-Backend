package com.increff.pos.dto;

import com.increff.pos.api.InventoryApi;
import com.increff.pos.api.ProductApi;
import com.increff.pos.dto.helper.DtoHelper;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.BulkInventoryData;
import com.increff.pos.model.data.OperationResponse;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.util.NormalizeUtil;
import com.increff.pos.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.increff.pos.flow.InventoryFlow;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import com.increff.pos.pojo.ProductPojo;
import java.util.Objects;

@Component
@Transactional
public class InventoryDto {
    private static final String ADD_OPERATION = "add";
    private static final String UPDATE_OPERATION = "update";

    @Autowired
    private InventoryApi inventoryApi;

    @Autowired
    private ProductApi productApi;

    @Autowired
    private InventoryFlow inventoryFlow;

    public void addInventory(InventoryForm form) throws ApiException {
        NormalizeUtil.normalize(form);
        ValidationUtil.validate(form);
        Integer productId = inventoryFlow.getProductIdFromForm(form);
        InventoryPojo pojo = DtoHelper.convertInventoryFormToPojo(form,productId);
        inventoryApi.addInventory(pojo);
    }

    public BulkInventoryData addInventoryFromList(List<InventoryForm> formList) {
        return processInventoryList(formList, ADD_OPERATION);
    }

    public BulkInventoryData updateInventoryFromList(List<InventoryForm> formList) {
        return processInventoryList(formList, UPDATE_OPERATION);
    }

    public void updateInventory(String barcode, InventoryForm form) throws ApiException {
        NormalizeUtil.normalize(form);
        ValidationUtil.validate(form);
        form.setProductBarcode(barcode.trim().toLowerCase());
        Integer productId = inventoryFlow.getProductIdFromForm(form);
        InventoryPojo existing = inventoryApi.getByProductId(productId);
        if (Objects.isNull(existing)) {
            InventoryPojo pojo = DtoHelper.convertInventoryFormToPojo(form,productId);
            inventoryApi.addInventory(pojo);
        } else {
            inventoryApi.updateInventory(productId, form.getQuantity());
        }
    }

    public List<InventoryData> getAll()  {
        List<InventoryPojo> pojoList = inventoryApi.getAll();
        List<ProductPojo> productList = productApi.getAll(0, Integer.MAX_VALUE);
        return DtoHelper.convertInventoryPojoListToData(pojoList, productList);
    }

    public InventoryData getInventoryByBarcode(String barcode) throws ApiException {
        ProductPojo product = inventoryFlow.getProductByBarcode(barcode.trim().toLowerCase());
        InventoryPojo inventory = getOrCreateInventory(product.getId());
        return DtoHelper.convertInventoryPojoToData(inventory, product);
    }

    private InventoryPojo getOrCreateInventory(Integer productId) throws ApiException {
        InventoryPojo inventory = inventoryApi.getByProductId(productId);
        if (Objects.isNull(inventory)) {
            inventory = new InventoryPojo();
            inventory.setProductId(productId);
            inventory.setQuantity(0);
        }
        return inventory;
    }

    private BulkInventoryData processInventoryList(List<InventoryForm> formList, String operation) {
        List<OperationResponse<InventoryForm>> successList = new ArrayList<>();
        List<OperationResponse<InventoryForm>> failureList = new ArrayList<>();
        for (InventoryForm form : formList) {
            try {
                NormalizeUtil.normalize(form);
                ValidationUtil.validate(form);
                if (operation.equals(ADD_OPERATION)) {
                    processAddInventory(form);
                    successList.add(OperationResponse.success(form, "Inventory added successfully."));
                } else if (operation.equals(UPDATE_OPERATION)) {
                    processUpdateInventory(form);
                    successList.add(OperationResponse.success(form, "Inventory updated successfully."));
                }
            } catch (ApiException e) {
                failureList.add(OperationResponse.failure(form, e.getMessage()));
            }
        }

        BulkInventoryData result = new BulkInventoryData();
        result.setSuccessList(successList);
        result.setFailureList(failureList);
        return result;
    }

    private void processAddInventory(InventoryForm form) throws ApiException {
        Integer productId = inventoryFlow.getProductIdFromForm(form);
        InventoryPojo pojo = DtoHelper.convertInventoryFormToPojo(form,productId);
        inventoryApi.addInventory(pojo);
    }

    private void processUpdateInventory(InventoryForm form) throws ApiException {
        Integer productId = inventoryFlow.getProductIdFromForm(form);
        InventoryPojo inventory = getOrCreateInventory(productId);
        inventoryApi.updateInventory(productId, form.getQuantity());
    }

}
