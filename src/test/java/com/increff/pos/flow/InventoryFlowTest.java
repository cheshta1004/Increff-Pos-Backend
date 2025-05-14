package com.increff.pos.flow;

import com.increff.pos.api.ProductApi;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.pojo.ProductPojo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class InventoryFlowTest {

    @InjectMocks
    private InventoryFlow inventoryFlow;

    @Mock
    private ProductApi productApi;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetProductByBarcode_success() throws ApiException {
        ProductPojo product = new ProductPojo();
        product.setId(1);
        product.setBarcode("abc123");
        when(productApi.getByBarcode("abc123")).thenReturn(product);
        ProductPojo result = inventoryFlow.getProductByBarcode("abc123");
        assertEquals("abc123", result.getBarcode());
    }

    @Test
    public void testGetProductIdFromForm_success() throws ApiException {
        InventoryForm form = new InventoryForm();
        form.setProductBarcode("abc123");
        ProductPojo product = new ProductPojo();
        product.setId(5);
        when(productApi.getByBarcode("abc123")).thenReturn(product);
        Integer id = inventoryFlow.getProductIdFromForm(form);
        assertEquals(Integer.valueOf(5), id);
    }
}