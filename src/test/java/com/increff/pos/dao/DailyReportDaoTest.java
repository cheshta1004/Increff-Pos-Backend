package com.increff.pos.dao;

import com.increff.pos.pojo.DailyReportPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.model.enums.OrderStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.List;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-test.xml"})
@WebAppConfiguration
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class DailyReportDaoTest {

    @Autowired
    private DailyReportDao dailyReportDao;

    @PersistenceContext
    private EntityManager em;

    private ZonedDateTime today;

    @Before
    public void setUp() {
        today = ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).withHour(0).withMinute(0).withSecond(0).withNano(0);
        
        // Create test client
        ClientPojo client = new ClientPojo();
        client.setClientName("Test Client");
        em.persist(client);
        em.flush();

        // Create test product
        ProductPojo product = new ProductPojo();
        product.setName("Test Product");
        product.setBarcode("TEST123");
        product.setMrp(100.0);
        product.setClientId(client.getId());
        product.setImageUrl("https://example.com/test-image.jpg");
        em.persist(product);
        em.flush();
        em.clear();  // Clear persistence context to ensure changes are written to database
    }

    @Test
    public void testInsertAndSelectByDate() {
        DailyReportPojo pojo = new DailyReportPojo();
        pojo.setDate(today);
        pojo.setOrderCount(5L);
        pojo.setTotalItems(10L);
        pojo.setRevenue(1000.0);
        dailyReportDao.insert(pojo);
        
        DailyReportPojo found = dailyReportDao.selectByDate(today);
        assertNotNull(found);
        assertEquals(Long.valueOf(5), found.getOrderCount());
        assertEquals(Long.valueOf(10), found.getTotalItems());
        assertEquals(Double.valueOf(1000.0), found.getRevenue());
    }

    @Test
    public void testSelectByDateNotFound() {
        DailyReportPojo found = dailyReportDao.selectByDate(today.plusDays(1));
        assertNull(found);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSelectByDateNullThrows() {
        dailyReportDao.selectByDate(null);
    }

    @Test
    public void testSelectAll() {
        for (int i = 0; i < 3; i++) {
            DailyReportPojo pojo = new DailyReportPojo();
            pojo.setDate(today.plusDays(i));
            pojo.setOrderCount((long) i);
            pojo.setTotalItems((long) (i * 2));
            pojo.setRevenue(i * 100.0);
            dailyReportDao.insert(pojo);
        }
        
        List<DailyReportPojo> all = dailyReportDao.selectAll();
        assertEquals(3, all.size());
        // Verify descending order by date
        assertTrue(all.get(0).getDate().isAfter(all.get(1).getDate()));
        assertTrue(all.get(1).getDate().isAfter(all.get(2).getDate()));
    }

    @Test
    public void testCalculateDailyReport_noOrders() {
        DailyReportPojo report = dailyReportDao.calculateDailyReport(today);
        assertNotNull(report);
        assertEquals(Long.valueOf(0), report.getOrderCount());
        assertEquals(Long.valueOf(0), report.getTotalItems());
        assertEquals(Double.valueOf(0.0), report.getRevenue());
    }

    @Test
    public void testCalculateDailyReport_withOrders() {
        // Insert a completed order and order item
        OrderPojo order = new OrderPojo();
        order.setTime(today.plusHours(1));
        order.setStatus(OrderStatus.COMPLETED);
        order.setCustomerName("customer");
        order.setCustomerContact("1234567890");
        em.persist(order);

        OrderItemPojo item = new OrderItemPojo();
        item.setOrderId(order.getId());
        item.setProductId(1);
        item.setQuantity(2);
        item.setSellingPrice(50.0);
        em.persist(item);
        em.flush();

        DailyReportPojo report = dailyReportDao.calculateDailyReport(today);
        assertEquals(Long.valueOf(1), report.getOrderCount());
        assertEquals(Long.valueOf(2), report.getTotalItems());
        assertEquals(Double.valueOf(100.0), report.getRevenue());
    }

    @Test
    public void testCalculateDailyReport_withMultipleOrders() {
        // Create multiple orders with different statuses
        for (int i = 0; i < 3; i++) {
            OrderPojo order = new OrderPojo();
            order.setTime(today.plusHours(i));
            order.setStatus(i % 2 == 0 ? OrderStatus.COMPLETED : OrderStatus.CREATED);
            order.setCustomerName("customer" + i);
            order.setCustomerContact("123456789" + i);
            em.persist(order);
            em.flush();

            OrderItemPojo item = new OrderItemPojo();
            item.setOrderId(order.getId());
            item.setProductId(1);
            item.setQuantity(i + 1);
            item.setSellingPrice(50.0);
            em.persist(item);
            em.flush();
        }

        DailyReportPojo report = dailyReportDao.calculateDailyReport(today);
        assertEquals(Long.valueOf(2), report.getOrderCount()); // Only COMPLETED orders
        assertEquals(Long.valueOf(4), report.getTotalItems()); // Sum of quantities for completed orders
        assertEquals(Double.valueOf(200.0), report.getRevenue()); // Sum of (quantity * price) for completed orders
    }

    @Test
    public void testRecalculateDailyReport_insertAndUpdate() {
        // Insert a completed order and order item
        OrderPojo order = new OrderPojo();
        order.setTime(today.plusHours(1));
        order.setStatus(OrderStatus.COMPLETED);
        order.setCustomerName("customer");
        order.setCustomerContact("1234567890");
        em.persist(order);

        OrderItemPojo item = new OrderItemPojo();
        item.setOrderId(order.getId());
        item.setProductId(1);
        item.setQuantity(2);
        item.setSellingPrice(50.0);
        em.persist(item);
        em.flush();

        // First recalc should insert
        dailyReportDao.recalculateDailyReport(today);
        DailyReportPojo found = dailyReportDao.selectByDate(today);
        assertNotNull(found);
        assertEquals(Long.valueOf(1), found.getOrderCount());

        // Second recalc should update
        order.setStatus(OrderStatus.COMPLETED);
        em.merge(order);
        item.setQuantity(3);
        em.merge(item);
        em.flush();

        dailyReportDao.recalculateDailyReport(today);
        found = dailyReportDao.selectByDate(today);
        assertNotNull(found);
        assertEquals(Long.valueOf(1), found.getOrderCount());
        assertEquals(Long.valueOf(3), found.getTotalItems());
        assertEquals(Double.valueOf(150.0), found.getRevenue());
    }

    @Test
    public void testCalculateDailyReport_withOrdersOutsideDateRange() {
        // Create orders for different days
        for (int i = -1; i <= 1; i++) {
            OrderPojo order = new OrderPojo();
            order.setTime(today.plusDays(i).withZoneSameInstant(ZoneId.of("Asia/Kolkata")));
            order.setStatus(OrderStatus.COMPLETED);
            order.setCustomerName("customer" + i);
            order.setCustomerContact("123456789" + i);
            em.persist(order);
            em.flush();

            OrderItemPojo item = new OrderItemPojo();
            item.setOrderId(order.getId());
            item.setProductId(1);
            item.setQuantity(1);
            item.setSellingPrice(50.0);
            em.persist(item);
            em.flush();
        }

        // Calculate report for today
        DailyReportPojo report = dailyReportDao.calculateDailyReport(today);
        assertEquals(Long.valueOf(1), report.getOrderCount()); // Only today's order
        assertEquals(Long.valueOf(1), report.getTotalItems());
        assertEquals(Double.valueOf(50.0), report.getRevenue());
    }
} 