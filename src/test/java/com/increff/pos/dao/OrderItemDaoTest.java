package com.increff.pos.dao;

import com.increff.pos.pojo.OrderItemPojo;
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
public class OrderItemDaoTest {

    @Autowired
    private OrderItemDao orderItemDao;

    private OrderItemPojo createOrderItem(int orderId, int productId, int quantity, double price) {
        OrderItemPojo pojo = new OrderItemPojo();
        pojo.setOrderId(orderId);
        pojo.setProductId(productId);
        pojo.setQuantity(quantity);
        pojo.setSellingPrice(price);
        return pojo;
    }

    @Test
    public void testInsertAndSelectByOrderId() {
        orderItemDao.insert(createOrderItem(1, 1, 2, 100.0));
        List<OrderItemPojo> items = orderItemDao.selectByOrderId(1);
        assertEquals(1, items.size());
        assertEquals(2, items.get(0).getQuantity().intValue());
    }
}