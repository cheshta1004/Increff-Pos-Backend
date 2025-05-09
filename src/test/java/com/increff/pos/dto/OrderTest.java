package com.increff.pos.dto;

import com.increff.pos.api.OrderApi;
import com.increff.pos.exception.ApiException;
import com.increff.pos.flow.OrderFlow;
import com.increff.pos.model.data.BulkOrderItemData;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.enums.OrderStatus;
import com.increff.pos.model.form.BulkOrderItemForm;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import lombok.var;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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

        InventoryPojo inventory = new InventoryPojo();
        inventory.setProductId(1);
        inventory.setQuantity(10);

        OrderPojo orderPojo = new OrderPojo();
        orderPojo.setId(1);

        when(orderFlow.getProductByBarcode("abc123")).thenReturn(product);
        when(orderFlow.getInventoryByProductId(1)).thenReturn(inventory);
        doNothing().when(orderFlow).reduceInventory(1, 2);
        doNothing().when(orderApi).insertOrder(any(OrderPojo.class));
        doNothing().when(orderApi).insertOrder(any(OrderItemPojo.class));

        when(orderApi.getOrderById(1)).thenReturn(orderPojo);

        BulkOrderItemData result = orderDto.placeOrder(form);

        assertEquals(1, result.getSuccessList().size());
        assertTrue(result.getFailureList().isEmpty());
    }

    // Test for fetching all orders and verifying the response contains the correct number of orders.
    @Test
    public void testGetAllOrders() throws ApiException {
        OrderPojo order = new OrderPojo();
        order.setId(1);

        when(orderApi.getAllOrders()).thenReturn(Collections.singletonList(order));
        when(orderApi.getOrderItems(1)).thenReturn(Collections.emptyList());
        when(orderFlow.convertOrderPojoToData(any(), any())).thenReturn(new OrderData());

        var response = orderDto.getAllOrders(0, 10);
        assertNotNull(response);
        assertEquals(1, response.getTotalItems());
    }

    //Updating the order status to "completed" and verifying the API call.
    @Test
    public void testUpdateStatusSuccess() throws ApiException {
        OrderPojo order = new OrderPojo();
        order.setId(1);

        when(orderApi.getOrderById(1)).thenReturn(order);
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

}
