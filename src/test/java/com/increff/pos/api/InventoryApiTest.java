package com.increff.pos.api;

import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.pojo.ClientPojo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-test.xml"})
@WebAppConfiguration
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class InventoryApiTest {

    @Autowired
    private InventoryApi inventoryApi;

    @Autowired
    private ProductApi productApi;

    @Autowired
    private ClientApi clientApi;

    private ProductPojo testProduct;
    private ClientPojo testClient;

    @Before
    public void setup() throws ApiException {
        // Create a test client first
        ClientPojo client = new ClientPojo();
        client.setClientName("testclient");
        clientApi.insertClient(client);
        List<ClientPojo> clients = clientApi.getClientsByPartialName("testclient", 0, 1);
        testClient = clients.get(0);

        // Create a test product
        ProductPojo product = new ProductPojo();
        product.setBarcode("123456");
        product.setName("Test Product");
        product.setMrp(99.99);
        product.setClientId(testClient.getId());
        product.setImageUrl("test.jpg");
        productApi.add(product);
        testProduct = productApi.getByBarcode("123456");
    }

    private InventoryPojo createInventory(Integer quantity) {
        InventoryPojo inventory = new InventoryPojo();
        inventory.setProductId(testProduct.getId());
        inventory.setQuantity(quantity);
        return inventory;
    }

    // Test to ensure inventory is added successfully with correct quantity and product ID
    @Test
    public void testAddInventory_success() throws ApiException {
        InventoryPojo inventory = createInventory(10);
        inventoryApi.addInventory(inventory);

        List<InventoryPojo> allInventory = inventoryApi.getAll();
        assertEquals(1, allInventory.size());
        assertEquals(Integer.valueOf(10), allInventory.get(0).getQuantity());
        assertEquals(testProduct.getId(), allInventory.get(0).getProductId());
    }

    // Test to check if inventory updates the existing quantity correctly
    @Test
    public void testAddInventory_updateExisting() throws ApiException {
        InventoryPojo inventory1 = createInventory(10);
        inventoryApi.addInventory(inventory1);

        InventoryPojo inventory2 = createInventory(20);
        inventoryApi.addInventory(inventory2);

        List<InventoryPojo> allInventory = inventoryApi.getAll();
        assertEquals(1, allInventory.size());
        assertEquals(Integer.valueOf(20), allInventory.get(0).getQuantity());
    }

    // Test to ensure inventory can be fetched successfully by product ID
    @Test
    public void testGetByProductId_success() throws ApiException {
        InventoryPojo inventory = createInventory(10);
        inventoryApi.addInventory(inventory);

        InventoryPojo retrieved = inventoryApi.getByProductId(testProduct.getId());
        assertNotNull(retrieved);
        assertEquals(Integer.valueOf(10), retrieved.getQuantity());
        assertEquals(testProduct.getId(), retrieved.getProductId());
    }

    // Test to verify inventory quantity can be updated correctly
    @Test
    public void testUpdateInventory_success() throws ApiException {
        InventoryPojo inventory = createInventory(10);
        inventoryApi.addInventory(inventory);

        inventoryApi.updateInventory(testProduct.getId(), 20);

        InventoryPojo updated = inventoryApi.getByProductId(testProduct.getId());
        assertEquals(Integer.valueOf(20), updated.getQuantity());
    }

    // Test to ensure ApiException is thrown when updating inventory with a negative quantity
    @Test(expected = ApiException.class)
    public void testUpdateInventory_negativeQuantity_shouldThrow() throws ApiException {
        InventoryPojo inventory = createInventory(10);
        inventoryApi.addInventory(inventory);

        inventoryApi.updateInventory(testProduct.getId(), -5); // Should throw ApiException
    }

    // Test to ensure ApiException is thrown when trying to update inventory for a non-existing product
    @Test(expected = ApiException.class)
    public void testUpdateInventory_notFound_shouldThrow() throws ApiException {
        inventoryApi.updateInventory(999, 10); // Should throw ApiException
    }

    // Test to verify inventory quantity can be reduced correctly
    @Test
    public void testReduceInventory_success() throws ApiException {
        InventoryPojo inventory = createInventory(10);
        inventoryApi.addInventory(inventory);

        inventoryApi.reduceInventory(testProduct.getId(), 5);

        InventoryPojo updated = inventoryApi.getByProductId(testProduct.getId());
        assertEquals(Integer.valueOf(5), updated.getQuantity());
    }

    // Test to ensure ApiException is thrown when trying to reduce inventory below zero
    @Test(expected = ApiException.class)
    public void testReduceInventory_insufficientQuantity_shouldThrow() throws ApiException {
        InventoryPojo inventory = createInventory(10);
        inventoryApi.addInventory(inventory);

        inventoryApi.reduceInventory(testProduct.getId(), 15);
    }

    // Test to ensure ApiException is thrown when trying to reduce inventory for a non-existing product
    @Test(expected = ApiException.class)
    public void testReduceInventory_notFound_shouldThrow() throws ApiException {
        inventoryApi.reduceInventory(999, 5);
    }
} 