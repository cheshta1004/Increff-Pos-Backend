package com.increff.pos.api;

import com.increff.pos.dto.ProductDto;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.pojo.ProductPojo;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = {"classpath:spring-test.xml"})
@WebAppConfiguration("src/main/webapp")
@Transactional
public class ProductApiTest {

    @Autowired
    private ProductApi productApi;

    @Autowired
    private ProductDto productDto;

    @Autowired
    private ClientApi clientApi;

    @PersistenceContext
    private EntityManager em;

    private ClientPojo testClient;

    @Before
    public void setup() throws ApiException {
        // Clear the database
        em.createQuery("DELETE FROM ProductPojo").executeUpdate();
        em.createQuery("DELETE FROM ClientPojo").executeUpdate();
        em.flush();

        // Create a test client
        ClientPojo client = new ClientPojo();
        client.setClientName("testclient");
        clientApi.insertClient(client);
        testClient = clientApi.getClientsByPartialName("testclient", 0, 1).get(0);
    }

    private ProductForm createProductForm(String barcode, String name, Double mrp) {
        ProductForm form = new ProductForm();
        form.setBarcode(barcode);
        form.setName(name);
        form.setMrp(mrp);
        form.setClientName("testclient");
        form.setImageUrl("test.jpg");
        return form;
    }

    @Test
    public void testAddProduct_success() throws ApiException {
        ProductForm form = createProductForm("123456", "Test Product", 99.99);
        productDto.insertProduct(form);

        List<ProductPojo> allProducts = productApi.getAll(0, 10);
        assertEquals(1, allProducts.size());
        assertEquals("123456", allProducts.get(0).getBarcode());
        assertEquals("test product", allProducts.get(0).getName());
        assertEquals(99.99, allProducts.get(0).getMrp(), 0.001);
    }

    @Test(expected = ApiException.class)
    public void testAddProduct_duplicateBarcode_shouldThrow() throws ApiException {
        ProductForm form1 = createProductForm("123456", "Test Product 1", 99.99);
        ProductForm form2 = createProductForm("123456", "Test Product 2", 88.88);
        productDto.insertProduct(form1);
        productDto.insertProduct(form2);
    }

    @Test
    public void testGetByBarcode_success() throws ApiException {
        ProductForm form = createProductForm("123456", "Test Product", 99.99);
        productDto.insertProduct(form);

        ProductPojo retrieved = productApi.getByBarcode("123456");
        assertNotNull(retrieved);
        assertEquals("123456", retrieved.getBarcode());
        assertEquals("test product", retrieved.getName());
        assertEquals(99.99, retrieved.getMrp(), 0.001);
    }

    @Test(expected = ApiException.class)
    public void testGetByBarcode_notFound() throws ApiException {
        productApi.getByBarcode("nonexistent");
    }

    @Test
    public void testGetByClientName_success() throws ApiException {
        ProductForm form1 = createProductForm("123456", "Test Product 1", 99.99);
        ProductForm form2 = createProductForm("789012", "Test Product 2", 88.88);
        productDto.insertProduct(form1);
        productDto.insertProduct(form2);

        List<ProductPojo> products = productApi.getByClientName("testclient", 0, 10);
        assertEquals(2, products.size());
    }

    @Test
    public void testGetByClientName_empty() throws ApiException {
        List<ProductPojo> products = productApi.getByClientName("nonexistent", 0, 10);
        assertTrue(products.isEmpty());
    }

    @Test
    public void testGetTotalCount() throws ApiException {
        ProductForm form1 = createProductForm("123456", "Test Product 1", 99.99);
        ProductForm form2 = createProductForm("789012", "Test Product 2", 88.88);
        productDto.insertProduct(form1);
        productDto.insertProduct(form2);

        assertEquals(2L, productApi.getTotalCount().longValue());
    }

    @Test
    public void testGetTotalCountByClientName() throws ApiException {
        ProductForm form1 = createProductForm("123456", "Test Product 1", 99.99);
        ProductForm form2 = createProductForm("789012", "Test Product 2", 88.88);
        productDto.insertProduct(form1);
        productDto.insertProduct(form2);

        assertEquals(2L, productApi.getTotalCountByClientName("testclient").longValue());
    }


}