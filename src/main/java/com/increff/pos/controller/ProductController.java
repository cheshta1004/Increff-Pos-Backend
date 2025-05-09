package com.increff.pos.controller;

import com.increff.pos.dto.ProductDto;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.data.ProductInsertResult;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.form.ProductForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductDto productDto;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    public ResponseEntity<?> insertProduct(@RequestBody ProductForm productForms) {
        try {
            productDto.insertProduct(productForms);
            return ResponseEntity.ok().build();
        } catch (ApiException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @RequestMapping(path = "/add-list", method = RequestMethod.POST)
    public ProductInsertResult insertBulkProducts(@RequestBody List<ProductForm> productForms) {
        return productDto.insertProductFromList(productForms);
    }

    @RequestMapping(path="/get",method = RequestMethod.GET)
    public PaginatedResponse<ProductData> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws ApiException {
        return productDto.getAllProducts(page, size);
    }

    @RequestMapping(path = "/barcode/{barcode}", method = RequestMethod.GET)
    public ProductData getProductByBarcode(@PathVariable String barcode) throws ApiException {
        return productDto.getProductByBarcode(barcode);
    }

    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public PaginatedResponse<ProductData> searchProducts(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String clientName,
            @RequestParam(required = false) String barcode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws ApiException {
        if (!Objects.isNull(searchTerm)) {
            return productDto.searchProducts(searchTerm, page, size);
        } else if (!Objects.isNull(clientName)) {
            return productDto.getProductsByClientName(clientName, page, size);
        } else if (!Objects.isNull(barcode)) {
            return productDto.getProductsByPartialBarcode(barcode, page, size);
        } else {
            return productDto.getAllProducts(page, size);
        }
    }
    @RequestMapping(path = "/update/{barcode}", method = RequestMethod.PUT)
    public void updateProductByBarcode(@PathVariable String barcode, @RequestBody ProductForm form) throws ApiException {
        productDto.updateProduct(barcode, form);
    }
}
