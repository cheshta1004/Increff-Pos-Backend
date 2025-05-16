package com.increff.pos.dao;

import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.model.enums.OrderStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.DirtiesContext;
import java.time.ZonedDateTime;
import java.util.List;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-test.xml"})
@WebAppConfiguration
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class OrderDaoTest {

    @Autowired
    private OrderDao orderDao;

    private OrderPojo createOrder(String customerName, String customerContact, OrderStatus status) {
        OrderPojo pojo = new OrderPojo();
        pojo.setTime(ZonedDateTime.now());
        pojo.setCustomerName(customerName);
        pojo.setCustomerContact(customerContact);
        pojo.setStatus(status);
        return pojo;
    }

    @Test
    public void testInsertAndSelect() {
        OrderPojo o = createOrder("Alice", "1234567890", OrderStatus.CREATED);
        orderDao.insert(o);
        OrderPojo found = orderDao.select(o.getId());
        assertNotNull(found);
        assertEquals("Alice", found.getCustomerName());
    }


}