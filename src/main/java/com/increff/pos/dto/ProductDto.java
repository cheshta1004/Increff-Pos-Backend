package com.increff.pos.dto;

import com.increff.pos.api.ProductApi;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.OperationResponse;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.data.ProductInsertResult;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.flow.ProductFlow;
import com.increff.pos.util.NormalizeUtil;
import com.increff.pos.util.ValidationUtil;
import com.increff.pos.dto.DtoHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductDto {

    @Autowired
    private ProductApi productApi;

    @Autowired
    private ProductFlow productFlow;

    public void insertProduct(ProductForm productForm) throws ApiException {
        ValidationUtil.validate(productForm);
        NormalizeUtil.normalize(productForm);
        ClientPojo client = productFlow.getClientByNameOrThrow(productForm.getClientName());
        ProductPojo pojo = DtoHelper.convertProductFormToPojo(productForm, client);
        productApi.add(pojo);
    }

    public ProductInsertResult insertProductFromList(List<ProductForm> productForms) {
        List<OperationResponse<ProductForm>> successList = new ArrayList<>();
        List<OperationResponse<ProductForm>> failureList = new ArrayList<>();
        for (ProductForm form : productForms) {
            try {
                insertProduct(form);
                successList.add(OperationResponse.success(form, "Product inserted successfully."));
            } catch (Exception e) {
                failureList.add(OperationResponse.failure(form, "Failed to insert product: " + e.getMessage()));
            }
        }
        ProductInsertResult result = new ProductInsertResult();
        result.setSuccessList(successList);
        result.setFailureList(failureList);
        return result;
    }

    public PaginatedResponse<ProductData> getAllProducts(int page, int size) throws ApiException {
        List<ProductPojo> pojoList = productApi.getAll(page, size);
        List<ProductData> dataList = new ArrayList<>();
        for (ProductPojo p : pojoList) {
            ClientPojo client = productFlow.getClientByIdOrThrow(p.getClientId());
            dataList.add(DtoHelper.convertProductPojoToData(p, client));
        }
        long totalItems = productApi.getTotalCount();
        int totalPages = (int) Math.ceil((double) totalItems / size);
        return new PaginatedResponse<>(dataList, page, totalPages, totalItems, size);
    }

    public ProductData getProductByBarcode(String barcode) throws ApiException {
        ProductPojo pojo = productApi.getByBarcode(barcode.trim().toLowerCase());
        ClientPojo client = productFlow.getClientByIdOrThrow(pojo.getClientId());
        return DtoHelper.convertProductPojoToData(pojo, client);
    }

    public PaginatedResponse<ProductData> getProductsByClientName(String clientName, int page, int size) throws ApiException {
        List<ProductPojo> pojoList = productApi.getByClientName(clientName, page, size);
        List<ProductData> dataList = new ArrayList<>();
        for (ProductPojo p : pojoList) {
            ClientPojo client = productFlow.getClientByIdOrThrow(p.getClientId());
            dataList.add(DtoHelper.convertProductPojoToData(p, client));
        }
        long totalItems = productApi.getTotalCountByClientName(clientName);
        int totalPages = (int) Math.ceil((double) totalItems / size);
        return new PaginatedResponse<>(dataList, page, totalPages, totalItems, size);
    }

    public void updateProduct(String barcode, ProductForm form) throws ApiException {
        ValidationUtil.validate(form);
        NormalizeUtil.normalize(form);
        productApi.updateByBarcode(barcode.trim().toLowerCase(), form.getName(), form.getMrp(), form.getImageUrl());
    }


    public PaginatedResponse<ProductData> getProductsByPartialBarcode(String barcode, int page, int size) throws ApiException {
        List<ProductPojo> pojoList = productApi.getByPartialBarcode(barcode, page, size);
        List<ProductData> dataList = new ArrayList<>();
        for (ProductPojo p : pojoList) {
            ClientPojo client = productFlow.getClientByIdOrThrow(p.getClientId());
            dataList.add(DtoHelper.convertProductPojoToData(p, client));
        }
        long totalItems = productApi.getTotalCountByPartialBarcode(barcode);
        int totalPages = (int) Math.ceil((double) totalItems / size);
        return new PaginatedResponse<>(dataList, page, totalPages, totalItems, size);
    }

    public PaginatedResponse<ProductData> searchProducts(String searchTerm, int page, int size) throws ApiException {
        List<ProductPojo> products = productApi.searchProducts(searchTerm, page, size);
        List<ProductData> productDataList = products.stream()
            .map(p -> {
                try {
                    ClientPojo client = productFlow.getClientByIdOrThrow(p.getClientId());
                    return DtoHelper.convertProductPojoToData(p, client);
                } catch (ApiException e) {
                    throw new RuntimeException(e);
                }
            })
            .collect(Collectors.toList());
        
        long totalItems = productApi.getTotalSearchResults(searchTerm);
        int totalPages = (int) Math.ceil((double) totalItems / size);
        
        return new PaginatedResponse<>(productDataList, page, size, totalItems, totalPages);
    }
}
