package com.increff.pos.dao;

import com.increff.pos.pojo.ProductPojo;
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
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.ClientPojo;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-test.xml"})
@WebAppConfiguration
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProductDaoTest {

    @Autowired
    private ProductDao productDao;

    @PersistenceContext
    private EntityManager entityManager;

    private ProductPojo createProduct(String barcode, String name, double mrp, int clientId) {
        ProductPojo pojo = new ProductPojo();
        pojo.setBarcode(barcode);
        pojo.setName(name);
        pojo.setMrp(mrp);
        pojo.setClientId(clientId);
        pojo.setImageUrl("img.jpg");
        return pojo;
    }

    @Test
    public void testInsertAndSelect() {
        ProductPojo p = createProduct("b1", "Product1", 100.0, 1);
        productDao.insert(p);
        ProductPojo found = productDao.select("barcode", "b1");
        assertNotNull(found);
        assertEquals("Product1", found.getName());
    }

    @Test
    public void testSelectAllPagination() {
        for (int i = 0; i < 5; i++) {
            productDao.insert(createProduct("b" + i, "P" + i, 100.0 + i, 1));
        }
        List<ProductPojo> page1 = productDao.selectAll(0, 2);
        List<ProductPojo> page2 = productDao.selectAll(1, 2);
        assertEquals(2, page1.size());
        assertEquals(2, page2.size());
    }

    @Test
    public void testSelectByClientName() throws ApiException {
        // Insert a client with name "client"
        ClientPojo client = new ClientPojo();
        client.setClientName("client");
        entityManager.persist(client); // or use your ClientDao if available
        entityManager.flush();
        int clientId = client.getId();

        // Insert products with the correct clientId
        productDao.insert(createProduct("b1", "Product1", 100.0, clientId));
        productDao.insert(createProduct("b2", "Product2", 150.0, clientId));

        List<ProductPojo> results = productDao.selectByClientName("client", 0, 10);
        assertEquals(2, results.size());
    }

    @Test
    public void testSelectByPartialBarcode() {
        productDao.insert(createProduct("abc123", "Product1", 100.0, 1));
        productDao.insert(createProduct("abc456", "Product2", 150.0, 1));
        List<ProductPojo> results = productDao.selectByPartialBarcode("abc", 0, 10);
        assertEquals(2, results.size());
    }

    @Test
    public void testGetTotalCount() {
        productDao.insert(createProduct("b1", "Product1", 100.0, 1));
        productDao.insert(createProduct("b2", "Product2", 150.0, 1));
        assertEquals(2, productDao.getTotalCount().intValue());
    }

  
}