package com.increff.pos.api;

import com.increff.pos.exception.ApiException;
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
public class OrderApiTest {

    @Autowired
    private OrderApi orderApi;

    @Autowired
    private ProductApi productApi;

    @Autowired
    private ClientApi clientApi;

    @PersistenceContext
    private EntityManager em;

    private ProductPojo testProduct1;
    private ProductPojo testProduct2;
    private ClientPojo testClient;
    private OrderPojo testOrder;

    @Before
    public void setup() throws ApiException {
        // Clear the database
        em.createQuery("DELETE FROM OrderItemPojo").executeUpdate();
        em.createQuery("DELETE FROM OrderPojo").executeUpdate();
        em.createQuery("DELETE FROM ProductPojo").executeUpdate();
        em.createQuery("DELETE FROM ClientPojo").executeUpdate();
        em.flush();

        // Create a test client
        ClientPojo client = new ClientPojo();
        client.setClientName("testclient");
        clientApi.insertClient(client);
        testClient = clientApi.getClientsByPartialName("testclient", 0, 1).get(0);

        // Create test products
        ProductPojo product1 = new ProductPojo();
        product1.setBarcode("123456");
        product1.setName("Test Product 1");
        product1.setMrp(99.99);
        product1.setClientId(testClient.getId());
        product1.setImageUrl("test1.jpg");
        productApi.add(product1);
        testProduct1 = productApi.getByBarcode("123456");

        ProductPojo product2 = new ProductPojo();
        product2.setBarcode("789012");
        product2.setName("Test Product 2");
        product2.setMrp(149.99);
        product2.setClientId(testClient.getId());
        product2.setImageUrl("test2.jpg");
        productApi.add(product2);
        testProduct2 = productApi.getByBarcode("789012");

        // Create a test order
        testOrder = new OrderPojo();
        testOrder.setTime(ZonedDateTime.now());
        testOrder.setStatus(OrderStatus.CREATED);
        testOrder.setCustomerName("Test Customer");
        testOrder.setCustomerContact("1234567890");
        orderApi.insertOrder(testOrder);
    }

    private OrderItemPojo createOrderItem(Integer productId, Integer quantity, Double sellingPrice) {
        OrderItemPojo item = new OrderItemPojo();
        item.setOrderId(testOrder.getId());
        item.setProductId(productId);
        item.setQuantity(quantity);
        item.setSellingPrice(sellingPrice);
        return item;
    }

    @Test
    public void testInsertOrder_success() throws ApiException {
        OrderPojo order = new OrderPojo();
        order.setTime(ZonedDateTime.now());
        order.setStatus(OrderStatus.CREATED);
        order.setCustomerName("New Customer");
        order.setCustomerContact("9876543210");
        orderApi.insertOrder(order);

        OrderPojo retrieved = orderApi.getOrderById(order.getId());
        assertNotNull(retrieved);
        assertEquals(OrderStatus.CREATED, retrieved.getStatus());
        assertEquals("New Customer", retrieved.getCustomerName());
        assertEquals("9876543210", retrieved.getCustomerContact());
    }

    @Test
    public void testInsertOrderItem_success() {
        OrderItemPojo item = createOrderItem(testProduct1.getId(), 2, 99.99);
        orderApi.insertOrder(item);

        List<OrderItemPojo> items = orderApi.getOrderItems(testOrder.getId());
        assertEquals(1, items.size());
        assertEquals(testProduct1.getId(), items.get(0).getProductId());
        assertEquals(Integer.valueOf(2), items.get(0).getQuantity());
        assertEquals(Double.valueOf(99.99), items.get(0).getSellingPrice());
    }

    @Test
    public void testUpdateStatus_success() throws ApiException {
        orderApi.updateStatus(testOrder.getId(), OrderStatus.COMPLETED);

        OrderPojo updatedOrder = orderApi.getOrderById(testOrder.getId());
        assertEquals(OrderStatus.COMPLETED, updatedOrder.getStatus());
    }

    @Test(expected = ApiException.class)
    public void testUpdateStatus_notFound_shouldThrow() throws ApiException {
        orderApi.updateStatus(999, OrderStatus.COMPLETED);
    }

    @Test(expected = ApiException.class)
    public void testUpdateStatus_completedOrder_shouldThrow() throws ApiException {
        // First complete the order
        orderApi.updateStatus(testOrder.getId(), OrderStatus.COMPLETED);
        
        // Try to update status again
        orderApi.updateStatus(testOrder.getId(), OrderStatus.CREATED);
    }

    @Test(expected = ApiException.class)
    public void testUpdateStatus_cancelledOrder_shouldThrow() throws ApiException {
        // First cancel the order
        orderApi.updateStatus(testOrder.getId(), OrderStatus.CANCELLED);
        
        // Try to update status again
        orderApi.updateStatus(testOrder.getId(), OrderStatus.CREATED);
    }

    @Test
    public void testGetAllOrders_success() {
        OrderPojo order = new OrderPojo();
        order.setTime(ZonedDateTime.now());
        order.setStatus(OrderStatus.CREATED);
        order.setCustomerName("Another Customer");
        order.setCustomerContact("5555555555");
        orderApi.insertOrder(order);

        List<OrderPojo> allOrders = orderApi.getAllOrders();
        assertEquals(2, allOrders.size());
    }

    @Test
    public void testGetOrderItems_success() {
        OrderItemPojo item1 = createOrderItem(testProduct1.getId(), 2, 99.99);
        OrderItemPojo item2 = createOrderItem(testProduct2.getId(), 1, 149.99);
        orderApi.insertOrder(item1);
        orderApi.insertOrder(item2);

        List<OrderItemPojo> orderItems = orderApi.getOrderItems(testOrder.getId());
        assertEquals(2, orderItems.size());
    }

    @Test
    public void testGetOrderById_success() throws ApiException {
        OrderPojo retrieved = orderApi.getOrderById(testOrder.getId());
        assertNotNull(retrieved);
        assertEquals(testOrder.getId(), retrieved.getId());
        assertEquals(testOrder.getCustomerName(), retrieved.getCustomerName());
        assertEquals(testOrder.getCustomerContact(), retrieved.getCustomerContact());
        assertEquals(testOrder.getStatus(), retrieved.getStatus());
    }

    @Test(expected = ApiException.class)
    public void testGetOrderById_notFound() throws ApiException {
        orderApi.getOrderById(999);
    }

    @Test
    public void testGetOrderItems_empty() {
        List<OrderItemPojo> items = orderApi.getOrderItems(testOrder.getId());
        assertTrue(items.isEmpty());
    }


} 