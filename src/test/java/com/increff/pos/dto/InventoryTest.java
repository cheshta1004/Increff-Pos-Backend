package com.increff.pos.dto;

import com.increff.pos.api.InventoryApi;
import com.increff.pos.api.ProductApi;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.BulkInventoryData;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.data.OperationResponse;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.flow.InventoryFlow;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InventoryTest {

    @InjectMocks
    private InventoryDto inventoryDto;

    @Mock
    private InventoryApi inventoryApi;

    @Mock
    private ProductApi productApi;

    @Mock
    private InventoryFlow inventoryFlow;

    private ProductPojo product;
    private InventoryPojo inventory;
    private InventoryForm form;

    @Before
    public void setUp() {
        // Setup test product
        product = new ProductPojo();
        product.setId(1);
        product.setBarcode("test123");
        product.setName("Test Product");
        product.setMrp(100.0);
        product.setClientId(1);

        // Setup test inventory
        inventory = new InventoryPojo();
        inventory.setId(1);
        inventory.setProductId(product.getId());
        inventory.setQuantity(10);

        // Setup test form
        form = new InventoryForm();
        form.setProductBarcode("test123");
        form.setQuantity(10);
    }

    @Test
    public void testAddInventory() throws ApiException {
        lenient().when(inventoryFlow.getProductIdFromForm(form)).thenReturn(product.getId());
        lenient().when(inventoryApi.getByProductId(product.getId())).thenReturn(null);

        inventoryDto.addInventory(form);

        verify(inventoryApi).addInventory(any(InventoryPojo.class));
    }

    @Test(expected = ApiException.class)
    public void testAddInventory_InvalidForm() throws ApiException {
        form.setQuantity(-1); // Invalid quantity
        inventoryDto.addInventory(form);
    }

    @Test
    public void testAddInventoryFromList() throws ApiException {
        List<InventoryForm> formList = new ArrayList<>();
        formList.add(form);

        lenient().when(inventoryFlow.getProductIdFromForm(form)).thenReturn(product.getId());
        lenient().when(inventoryApi.getByProductId(product.getId())).thenReturn(null);

        BulkInventoryData result = inventoryDto.addInventoryFromList(formList);

        assertNotNull(result);
        assertEquals(1, result.getSuccessList().size());
        assertEquals(0, result.getFailureList().size());
        verify(inventoryApi).addInventory(any(InventoryPojo.class));
    }

    @Test
    public void testAddInventoryFromList_WithFailures() throws ApiException {
        List<InventoryForm> formList = new ArrayList<>();
        formList.add(form);

        InventoryForm invalidForm = new InventoryForm();
        invalidForm.setProductBarcode("test123");
        invalidForm.setQuantity(-1); // Invalid quantity
        formList.add(invalidForm);

        lenient().when(inventoryFlow.getProductIdFromForm(form)).thenReturn(product.getId());
        lenient().when(inventoryApi.getByProductId(product.getId())).thenReturn(null);

        BulkInventoryData result = inventoryDto.addInventoryFromList(formList);

        assertNotNull(result);
        assertEquals(1, result.getSuccessList().size());
        assertEquals(1, result.getFailureList().size());
    }

    @Test
    public void testUpdateInventory() throws ApiException {
        lenient().when(inventoryFlow.getProductIdFromForm(form)).thenReturn(product.getId());
        lenient().when(inventoryApi.getByProductId(product.getId())).thenReturn(inventory);

        inventoryDto.updateInventory("test123", form);

        verify(inventoryApi).updateInventory(product.getId(), form.getQuantity());
    }

    @Test
    public void testUpdateInventory_NewInventory() throws ApiException {
        lenient().when(inventoryFlow.getProductIdFromForm(form)).thenReturn(product.getId());
        lenient().when(inventoryApi.getByProductId(product.getId())).thenReturn(null);

        inventoryDto.updateInventory("test123", form);

        verify(inventoryApi).addInventory(any(InventoryPojo.class));
    }

    @Test
    public void testUpdateInventoryFromList() throws ApiException {
        List<InventoryForm> formList = new ArrayList<>();
        formList.add(form);

        lenient().when(inventoryFlow.getProductIdFromForm(form)).thenReturn(product.getId());
        lenient().when(inventoryApi.getByProductId(product.getId())).thenReturn(inventory);

        BulkInventoryData result = inventoryDto.updateInventoryFromList(formList);

        assertNotNull(result);
        assertEquals(1, result.getSuccessList().size());
        assertEquals(0, result.getFailureList().size());
        verify(inventoryApi).updateInventory(product.getId(), form.getQuantity());
    }

    @Test
    public void testGetAll() throws ApiException {
        List<InventoryPojo> inventoryList = new ArrayList<>();
        inventoryList.add(inventory);

        List<ProductPojo> productList = new ArrayList<>();
        productList.add(product);

        lenient().when(inventoryApi.getAll()).thenReturn(inventoryList);
        lenient().when(productApi.getAll(0, Integer.MAX_VALUE)).thenReturn(productList);

        List<InventoryData> result = inventoryDto.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(product.getBarcode(), result.get(0).getBarcode());
        assertEquals(product.getName(), result.get(0).getProductName());
        assertEquals(inventory.getQuantity(), result.get(0).getQuantity());
    }

    @Test
    public void testGetInventoryByBarcode() throws ApiException {
        lenient().when(inventoryFlow.getProductByBarcode("test123")).thenReturn(product);
        lenient().when(inventoryApi.getByProductId(product.getId())).thenReturn(inventory);

        InventoryData result = inventoryDto.getInventoryByBarcode("test123");

        assertNotNull(result);
        assertEquals(product.getBarcode(), result.getBarcode());
        assertEquals(product.getName(), result.getProductName());
        assertEquals(inventory.getQuantity(), result.getQuantity());
    }

    @Test(expected = ApiException.class)
    public void testGetInventoryByBarcode_ProductNotFound() throws ApiException {
        lenient().when(inventoryFlow.getProductByBarcode("nonexistent")).thenThrow(new ApiException("Product not found"));
        inventoryDto.getInventoryByBarcode("nonexistent");
    }

    @Test
    public void testProcessInventoryList_AddOperation() throws ApiException {
        List<InventoryForm> formList = new ArrayList<>();
        formList.add(form);

        lenient().when(inventoryFlow.getProductIdFromForm(form)).thenReturn(product.getId());
        lenient().when(inventoryApi.getByProductId(product.getId())).thenReturn(null);

        BulkInventoryData result = inventoryDto.addInventoryFromList(formList);

        assertNotNull(result);
        assertEquals(1, result.getSuccessList().size());
        assertEquals(0, result.getFailureList().size());
        verify(inventoryApi).addInventory(any(InventoryPojo.class));
    }

    @Test
    public void testProcessInventoryList_UpdateOperation() throws ApiException {
        List<InventoryForm> formList = new ArrayList<>();
        formList.add(form);

        lenient().when(inventoryFlow.getProductIdFromForm(form)).thenReturn(product.getId());
        lenient().when(inventoryApi.getByProductId(product.getId())).thenReturn(inventory);

        BulkInventoryData result = inventoryDto.updateInventoryFromList(formList);

        assertNotNull(result);
        assertEquals(1, result.getSuccessList().size());
        assertEquals(0, result.getFailureList().size());
        verify(inventoryApi).updateInventory(product.getId(), form.getQuantity());
    }
} 