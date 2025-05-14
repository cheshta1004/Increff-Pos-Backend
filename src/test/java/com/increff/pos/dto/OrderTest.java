package com.increff.pos.dto;

import com.increff.pos.api.OrderApi;
import com.increff.pos.exception.ApiException;
import com.increff.pos.flow.OrderFlow;
import com.increff.pos.model.data.BulkOrderItemData;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.enums.OrderStatus;
import com.increff.pos.model.form.BulkOrderItemForm;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.Collections;
import java.time.ZonedDateTime;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderTest {

    @InjectMocks
    private OrderDto orderDto;

    @Mock
    private OrderApi orderApi;

    @Mock
    private OrderFlow orderFlow;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    //placing an order successfully, ensuring inventory is reduced and order is inserted correctly.
    @Test
    public void testPlaceOrderSuccess() throws ApiException {
        // Create test data
        BulkOrderItemForm form = new BulkOrderItemForm();
        OrderItemForm item = new OrderItemForm();
        item.setBarcode("abc123");
        item.setQuantity(2);
        item.setSellingPrice(100.0);
        form.setOrderItems(Collections.singletonList(item));
        form.setCustomerName("Test Customer");
        form.setCustomerContact("9999999999");

        ProductPojo product = new ProductPojo();
        product.setId(1);
        product.setBarcode("abc123");
        product.setName("Test Product");

        // Set up mocks
        when(orderFlow.getProductByBarcode("abc123")).thenReturn(product);
        doNothing().when(orderFlow).reduceInventory(1, 2);
        doNothing().when(orderApi).insertOrder(any(OrderPojo.class));
        doNothing().when(orderApi).insertOrder(any(OrderItemPojo.class));

        // Execute test
        BulkOrderItemData result = orderDto.placeOrder(form);

        // Verify results
        assertEquals(1, result.getSuccessList().size());
        assertTrue(result.getFailureList().isEmpty());
        verify(orderFlow).getProductByBarcode("abc123");
        verify(orderFlow).reduceInventory(1, 2);
        verify(orderApi).insertOrder(any(OrderPojo.class));
        verify(orderApi).insertOrder(any(OrderItemPojo.class));
    }

    // Test for fetching all orders and verifying the response contains the correct number of orders.
    @Test
    public void testGetAllOrders() throws ApiException {
        // Create test data
        OrderPojo order = new OrderPojo();
        order.setId(1);
        order.setTime(ZonedDateTime.now());
        order.setStatus(OrderStatus.CREATED);
        order.setCustomerName("Test Customer");
        order.setCustomerContact("1234567890");

        OrderItemPojo orderItem = new OrderItemPojo();
        orderItem.setId(1);
        orderItem.setOrderId(1);
        orderItem.setProductId(1);
        orderItem.setQuantity(2);
        orderItem.setSellingPrice(100.0);

        ProductPojo product = new ProductPojo();
        product.setId(1);
        product.setBarcode("abc123");
        product.setName("Test Product");

        // Set up mocks
        when(orderApi.getAllOrders()).thenReturn(Collections.singletonList(order));
        when(orderApi.getOrderItems(1)).thenReturn(Collections.singletonList(orderItem));
        when(orderFlow.getProductById(1)).thenReturn(product);

        // Call the method
        PaginatedResponse<OrderData> response = orderDto.getAllOrders(0, 10);

        // Verify the response
        assertNotNull(response);
        assertEquals(1L, response.getTotalItems());
        assertEquals(1, response.getContent().size());
        assertEquals(Integer.valueOf(1), response.getContent().get(0).getId());
        assertEquals("Test Customer", response.getContent().get(0).getCustomerName());
    }

    //Updating the order status to "completed" and verifying the API call.
    @Test
    public void testUpdateStatusSuccess() throws ApiException {
        doNothing().when(orderApi).updateStatus(1, OrderStatus.COMPLETED);

        orderDto.updateStatus(1, "completed");

        verify(orderApi, times(1)).updateStatus(1, OrderStatus.COMPLETED);
    }

    // Verify that an ApiException is thrown when attempting to update the order status to an invalid value.
    @Test
    public void testUpdateStatusInvalid() {
        ApiException exception = assertThrows(ApiException.class, () ->
                orderDto.updateStatus(1, "invalidStatus")
        );
        assertTrue(exception.getMessage().contains("Invalid order status"));
    }

    @Test
    public void testUpdateStatus_invalidStatus_shouldThrow() {
        ApiException exception = assertThrows(ApiException.class, () ->
                orderDto.updateStatus(1, "invalidStatus")
        );
        assertEquals("Invalid order status: invalidStatus. Valid statuses are: CREATED, COMPLETED, CANCELLED.", exception.getMessage());
    }

    @Test(expected = ApiException.class)
    public void testGetOrderById_notFound_shouldThrow() throws ApiException {
        // Mock the orderApi to return null for a non-existent order
        when(orderApi.getOrderById(999)).thenReturn(null);
        // This should throw ApiException because OrderApi.getOrderById throws when order is null
        orderDto.getOrderById(999);
    }

}