package com.increff.pos.api;

import com.increff.pos.exception.ApiException;
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
public class ProductApiTest {

    @Autowired
    private ProductApi productApi;

    @Autowired
    private ClientApi clientApi;

    private ClientPojo testClient;

    @Before
    public void setup() throws ApiException {
        // Create a test client first
        ClientPojo client = new ClientPojo();
        client.setClientName("testclient");
        clientApi.insertClient(client);
        List<ClientPojo> clients = clientApi.getClientsByPartialName("testclient", 0, 1);
        testClient = clients.get(0);
    }

    private ProductPojo createProduct(String barcode, String name, Double mrp) {
        ProductPojo product = new ProductPojo();
        // Normalize data
        product.setBarcode(barcode.trim().toLowerCase());
        product.setName(name.trim().toLowerCase());
        product.setMrp(mrp);
        product.setClientId(testClient.getId());
        product.setImageUrl("test.jpg");
        return product;
    }

    // Test to verify that a product can be added successfully and retrieved correctly
    @Test
    public void testAddProduct_success() throws ApiException {
        ProductPojo product = createProduct("123456", "Test Product", 99.99);
        productApi.add(product);

        List<ProductPojo> allProducts = productApi.getAll(0, 10);
        assertEquals(1, allProducts.size());
        assertEquals("123456", allProducts.get(0).getBarcode());
        assertEquals("test product", allProducts.get(0).getName());
    }

    // Test to ensure adding a product with a duplicate barcode throws an exception
    @Test(expected = ApiException.class)
    public void testAddProduct_duplicateBarcode_shouldThrow() throws ApiException {
        ProductPojo product1 = createProduct("123456", "Product 1", 99.99);
        productApi.add(product1);
        
        ProductPojo product2 = createProduct("123456", "Product 2", 149.99);
        productApi.add(product2); // Should throw ApiException
    }

    // Test to verify a product can be retrieved by its barcode
    @Test
    public void testGetByBarcode_success() throws ApiException {
        ProductPojo product = createProduct("123456", "Test Product", 99.99);
        productApi.add(product);

        ProductPojo retrieved = productApi.getByBarcode("123456");
        assertEquals("123456", retrieved.getBarcode());
        assertEquals("test product", retrieved.getName());
    }

    // Test to ensure an exception is thrown when trying to retrieve a product with a non-existent barcode
    @Test(expected = ApiException.class)
    public void testGetByBarcode_notFound_shouldThrow() throws ApiException {
        productApi.getByBarcode("nonexistent"); // Should throw ApiException
    }

    // Test to verify that products can be retrieved by client name
    @Test
    public void testGetByClientName_success() throws ApiException {
        ProductPojo product1 = createProduct("123456", "Product 1", 99.99);
        ProductPojo product2 = createProduct("789012", "Product 2", 149.99);
        
        productApi.add(product1);
        productApi.add(product2);

        List<ProductPojo> clientProducts = productApi.getByClientName("testclient", 0, 10);
        assertEquals(2, clientProducts.size());
    }

    // Test to ensure products can be retrieved using a partial barcode
    @Test
    public void testGetByPartialBarcode_success() throws ApiException {
        ProductPojo product1 = createProduct("123456", "Product 1", 99.99);
        ProductPojo product2 = createProduct("123789", "Product 2", 149.99);
        
        productApi.add(product1);
        productApi.add(product2);

        List<ProductPojo> products = productApi.getByPartialBarcode("123", 0, 10);
        assertEquals(2, products.size());
    }

    // Test to verify that a product can be updated by its barcode
    @Test
    public void testUpdateByBarcode_success() throws ApiException {
        ProductPojo product = createProduct("123456", "Original Name", 99.99);
        productApi.add(product);

        productApi.updateByBarcode("123456", "Updated Name", 149.99, "new-image.jpg");

        ProductPojo updated = productApi.getByBarcode("123456");
        assertEquals("Updated Name", updated.getName());
        assertEquals(149.99, updated.getMrp(), 0.001);
        assertEquals("new-image.jpg", updated.getImageUrl());
    }

    // Test to verify that the total product count can be retrieved correctly
    @Test
    public void testGetTotalCount() throws ApiException {
        ProductPojo product1 = createProduct("123456", "Product 1", 99.99);
        ProductPojo product2 = createProduct("789012", "Product 2", 149.99);
        
        productApi.add(product1);
        productApi.add(product2);

        assertEquals(Long.valueOf(2), productApi.getTotalCount());
    }
} 