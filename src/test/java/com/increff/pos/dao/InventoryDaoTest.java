package com.increff.pos.dao;

import com.increff.pos.pojo.InventoryPojo;
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
public class InventoryDaoTest {

    @Autowired
    private InventoryDao inventoryDao;

    private InventoryPojo createInventory(int productId, int quantity) {
        InventoryPojo pojo = new InventoryPojo();
        pojo.setProductId(productId);
        pojo.setQuantity(quantity);
        return pojo;
    }

    @Test
    public void testInsertAndSelect() {
        InventoryPojo inv = createInventory(1, 10);
        inventoryDao.insert(inv);
        InventoryPojo found = inventoryDao.select("productId", 1);
        assertNotNull(found);
        assertEquals(10, found.getQuantity().intValue());
    }

    @Test
    public void testSelectAll() {
        inventoryDao.insert(createInventory(1, 10));
        inventoryDao.insert(createInventory(2, 20));
        List<InventoryPojo> all = inventoryDao.selectAll();
        assertTrue(all.size() >= 2);
    }
}