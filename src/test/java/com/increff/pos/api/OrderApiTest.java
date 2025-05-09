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

    private ProductPojo testProduct1;
    private ProductPojo testProduct2;
    private ClientPojo testClient;
    private OrderPojo testOrder;

    @Before
    public void setup() throws ApiException {
        // Create a test client first
        ClientPojo client = new ClientPojo();
        client.setClientName("testclient");
        clientApi.insertClient(client);
        List<ClientPojo> clients = clientApi.getClientsByPartialName("testclient", 0, 1);
        testClient = clients.get(0);

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
        OrderPojo order = new OrderPojo();
        order.setTime(ZonedDateTime.now());
        order.setStatus(OrderStatus.CREATED);
        order.setCustomerName("Test Customer");
        order.setCustomerContact("1234567890");
        orderApi.insertOrder(order);
        testOrder = orderApi.getAllOrders().get(0);
    }

    private OrderItemPojo createOrderItem(Integer productId, Integer quantity, Double sellingPrice) {
        OrderItemPojo orderItem = new OrderItemPojo();
        orderItem.setOrderId(testOrder.getId());
        orderItem.setProductId(productId);
        orderItem.setQuantity(quantity);
        orderItem.setSellingPrice(sellingPrice);
        return orderItem;
    }

    // Test to verify a new order is inserted successfully and can be retrieved
    @Test
    public void testInsertOrder_success() {
        OrderPojo order = new OrderPojo();
        order.setTime(ZonedDateTime.now());
        order.setStatus(OrderStatus.CREATED);
        order.setCustomerName("New Customer");
        order.setCustomerContact("9876543210");
        
        orderApi.insertOrder(order);
        
        List<OrderPojo> allOrders = orderApi.getAllOrders();
        assertEquals(2, allOrders.size());
        assertEquals("New Customer", allOrders.get(1).getCustomerName());
    }

    // Test to ensure an order item is added correctly to an order
    @Test
    public void testInsertOrderItem_success() {
        OrderItemPojo orderItem = createOrderItem(testProduct1.getId(), 2, 99.99);
        orderApi.insertOrder(orderItem);
        
        List<OrderItemPojo> orderItems = orderApi.getOrderItems(testOrder.getId());
        assertEquals(1, orderItems.size());
        assertEquals(Integer.valueOf(2), orderItems.get(0).getQuantity());
        assertEquals(Double.valueOf(99.99), orderItems.get(0).getSellingPrice());
    }

    // Test to verify that the order status can be updated successfully
    @Test
    public void testUpdateStatus_success() {
        orderApi.updateStatus(testOrder.getId(), OrderStatus.COMPLETED);
        
        OrderPojo updatedOrder = orderApi.getOrderById(testOrder.getId());
        assertEquals(OrderStatus.COMPLETED, updatedOrder.getStatus());
    }

    // Test to ensure all orders can be fetched successfully
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

    // Test to verify that multiple order items can be retrieved for a single order
    @Test
    public void testGetOrderItems_success() {
        OrderItemPojo item1 = createOrderItem(testProduct1.getId(), 2, 99.99);
        OrderItemPojo item2 = createOrderItem(testProduct2.getId(), 1, 149.99);
        orderApi.insertOrder(item1);
        orderApi.insertOrder(item2);
        
        List<OrderItemPojo> orderItems = orderApi.getOrderItems(testOrder.getId());
        assertEquals(2, orderItems.size());
    }

    // Test to ensure orders can be fetched based on their status
    @Test
    public void testGetOrdersByStatus_success() {
        OrderPojo order = new OrderPojo();
        order.setTime(ZonedDateTime.now());
        order.setStatus(OrderStatus.COMPLETED);
        order.setCustomerName("Another Customer");
        order.setCustomerContact("5555555555");
        orderApi.insertOrder(order);
        
        List<OrderPojo> createdOrders = orderApi.getOrdersByStatus(OrderStatus.CREATED);
        assertEquals(1, createdOrders.size());
        
        List<OrderPojo> completedOrders = orderApi.getOrdersByStatus(OrderStatus.COMPLETED);
        assertEquals(1, completedOrders.size());
    }

    // Test to verify that an order can be retrieved successfully by its ID
    @Test
    public void testGetOrderById_success() {
        OrderPojo retrievedOrder = orderApi.getOrderById(testOrder.getId());
        assertNotNull(retrievedOrder);
        assertEquals(testOrder.getCustomerName(), retrievedOrder.getCustomerName());
        assertEquals(testOrder.getCustomerContact(), retrievedOrder.getCustomerContact());
    }
} 