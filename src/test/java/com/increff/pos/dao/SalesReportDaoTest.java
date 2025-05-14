package com.increff.pos.dao;

import com.increff.pos.model.data.SalesReportData;
import com.increff.pos.model.form.SalesReportFilterForm;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
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
import java.util.List;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-test.xml"})
@WebAppConfiguration
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class SalesReportDaoTest {

    @Autowired
    private SalesReportDao salesReportDao;

    @PersistenceContext
    private EntityManager em;

    private ZonedDateTime today;
    private ClientPojo client;
    private ProductPojo product;
    private OrderPojo order;
    private OrderItemPojo orderItem;

    @Before
    public void setUp() {
        today = ZonedDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        
        // Create test client
        client = new ClientPojo();
        client.setClientName("Test Client");
        em.persist(client);
        em.flush();

        // Create test product
        product = new ProductPojo();
        product.setName("Test Product");
        product.setBarcode("TEST123");
        product.setMrp(100.0);
        product.setClientId(client.getId());
        product.setImageUrl("https://example.com/test-image.jpg");
        em.persist(product);
        em.flush();

        // Create test order
        order = new OrderPojo();
        order.setTime(today);
        order.setStatus(OrderStatus.COMPLETED);
        order.setCustomerName("Test Customer");
        order.setCustomerContact("1234567890");
        em.persist(order);
        em.flush();

        // Create test order item
        orderItem = new OrderItemPojo();
        orderItem.setOrderId(order.getId());
        orderItem.setProductId(product.getId());
        orderItem.setQuantity(2);
        orderItem.setSellingPrice(50.0);
        em.persist(orderItem);
        em.flush();
    }

    @Test
    public void testGetFilteredSalesReport_AllOrders() {
        SalesReportFilterForm form = new SalesReportFilterForm();
        form.setStartDate(today.minusDays(1));
        form.setEndDate(today.plusDays(1));

        List<SalesReportData> report = salesReportDao.getFilteredSalesReport(form);
        assertNotNull(report);
        assertEquals(1, report.size());
        
        SalesReportData data = report.get(0);
        assertEquals("Test Client", data.getClientName());
        assertEquals("Test Product", data.getProductName());
        assertEquals("TEST123", data.getBarcode());
        assertEquals(Long.valueOf(2), data.getQuantity());
        assertEquals(Double.valueOf(100.0), data.getRevenue());
    }

    @Test
    public void testGetFilteredSalesReport_WithClientFilter() {
        SalesReportFilterForm form = new SalesReportFilterForm();
        form.setStartDate(today.minusDays(1));
        form.setEndDate(today.plusDays(1));
        form.setClientName("Test Client");

        List<SalesReportData> report = salesReportDao.getFilteredSalesReport(form);
        assertNotNull(report);
        assertEquals(1, report.size());
        assertEquals("Test Client", report.get(0).getClientName());
    }

    @Test
    public void testGetFilteredSalesReport_NoMatchingOrders() {
        SalesReportFilterForm form = new SalesReportFilterForm();
        form.setStartDate(today.plusDays(2));
        form.setEndDate(today.plusDays(3));

        List<SalesReportData> report = salesReportDao.getFilteredSalesReport(form);
        assertNotNull(report);
        assertTrue(report.isEmpty());
    }

    @Test
    public void testGetFilteredSalesReport_MultipleOrders() {
        // Create another order for the same product
        OrderPojo order2 = new OrderPojo();
        order2.setTime(today);
        order2.setStatus(OrderStatus.COMPLETED);
        order2.setCustomerName("Test Customer 2");
        order2.setCustomerContact("0987654321");
        em.persist(order2);
        em.flush();

        OrderItemPojo item2 = new OrderItemPojo();
        item2.setOrderId(order2.getId());
        item2.setProductId(product.getId());
        item2.setQuantity(3);
        item2.setSellingPrice(50.0);
        em.persist(item2);
        em.flush();

        SalesReportFilterForm form = new SalesReportFilterForm();
        form.setStartDate(today.minusDays(1));
        form.setEndDate(today.plusDays(1));

        List<SalesReportData> report = salesReportDao.getFilteredSalesReport(form);
        assertNotNull(report);
        assertEquals(1, report.size());
        
        SalesReportData data = report.get(0);
        assertEquals(Long.valueOf(5), data.getQuantity()); // 2 + 3
        assertEquals(Double.valueOf(250.0), data.getRevenue()); // (2 * 50) + (3 * 50)
    }

    @Test
    public void testGetFilteredSalesReport_NonCompletedOrders() {
        // Create an order with CREATED status
        OrderPojo order2 = new OrderPojo();
        order2.setTime(today);
        order2.setStatus(OrderStatus.CREATED);
        order2.setCustomerName("Test Customer 2");
        order2.setCustomerContact("0987654321");
        em.persist(order2);
        em.flush();

        OrderItemPojo item2 = new OrderItemPojo();
        item2.setOrderId(order2.getId());
        item2.setProductId(product.getId());
        item2.setQuantity(3);
        item2.setSellingPrice(50.0);
        em.persist(item2);
        em.flush();

        SalesReportFilterForm form = new SalesReportFilterForm();
        form.setStartDate(today.minusDays(1));
        form.setEndDate(today.plusDays(1));

        List<SalesReportData> report = salesReportDao.getFilteredSalesReport(form);
        assertNotNull(report);
        assertEquals(1, report.size());
        
        // Should only include the COMPLETED order
        SalesReportData data = report.get(0);
        assertEquals(Long.valueOf(2), data.getQuantity());
        assertEquals(Double.valueOf(100.0), data.getRevenue());
    }
}
