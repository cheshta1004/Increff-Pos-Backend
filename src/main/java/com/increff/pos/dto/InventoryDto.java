package com.increff.pos.dto;

import com.increff.pos.api.InventoryApi;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.BulkInventoryData;
import com.increff.pos.model.data.OperationResponse;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.util.NormalizeUtil;
import com.increff.pos.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.increff.pos.flow.InventoryFlow;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import com.increff.pos.pojo.ProductPojo;
import java.util.Objects;
@Service
@Transactional
public class InventoryDto {
    private static final String ADD_OPERATION = "add";
    private static final String UPDATE_OPERATION = "update";

    @Autowired
    private InventoryApi inventoryApi;

    @Autowired
    private InventoryFlow inventoryFlow;

    public void addInventory(InventoryForm form) throws ApiException {
        NormalizeUtil.normalize(form);
        ValidationUtil.validate(form);

        Integer productId = inventoryFlow.getProductIdFromForm(form);
        InventoryPojo pojo = new InventoryPojo();
        pojo.setProductId(productId);
        pojo.setQuantity(form.getQuantity());

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
        form.setProductBarcode(barcode);
        Integer productId = inventoryFlow.getProductIdFromForm(form);
        InventoryPojo existing = inventoryApi.getByProductId(productId);
        if (Objects.isNull(existing)) {
            InventoryPojo pojo = new InventoryPojo();
            pojo.setProductId(productId);
            pojo.setQuantity(form.getQuantity());
            inventoryApi.addInventory(pojo);
        } else {
            inventoryApi.updateInventory(productId, form.getQuantity());
        }
    }

    public List<InventoryData> getAll() throws ApiException {
        List<InventoryPojo> pojoList = inventoryApi.getAll();
        List<InventoryData> dataList = new ArrayList<>();
        for (InventoryPojo pojo : pojoList) {
            dataList.add(DtoHelper.convertInventoryPojoToData(pojo));
        }
        return dataList;
    }

    public InventoryData getInventoryByBarcode(String barcode) throws ApiException {
        ProductPojo product = inventoryFlow.getProductByBarcode(barcode);
        if (Objects.isNull(product)) {
            throw new ApiException("Product not found for barcode: " + barcode);
        }
        InventoryPojo inventory = inventoryApi.getByProductId(product.getId());
        if (Objects.isNull(inventory)) {
            InventoryData data = new InventoryData();
            data.setId(null);
            data.setProductId(product.getId());
            data.setQuantity(0);
            return data;
        }
        return DtoHelper.convertInventoryPojoToData(inventory);
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
        InventoryPojo pojo = new InventoryPojo();
        pojo.setProductId(productId);
        pojo.setQuantity(form.getQuantity());
        inventoryApi.addInventory(pojo);
    }

    private void processUpdateInventory(InventoryForm form) throws ApiException {
        Integer productId = inventoryFlow.getProductIdFromForm(form);
        inventoryApi.updateInventory(productId, form.getQuantity());
    }
}
