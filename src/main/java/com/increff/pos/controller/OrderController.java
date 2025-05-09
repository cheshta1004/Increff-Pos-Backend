package com.increff.pos.controller;

import com.increff.pos.dto.OrderDto;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.BulkOrderItemData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.form.BulkOrderItemForm;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.OrderItemData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping(path = "/api/order")
public class OrderController {

    @Autowired
    private OrderDto orderDto;

    @RequestMapping(path = "/place-bulk", method = RequestMethod.POST)
    public BulkOrderItemData placeOrder(@RequestBody BulkOrderItemForm form) throws ApiException {
        return orderDto.placeOrder(form);
    }

    @RequestMapping(path="/get",method = RequestMethod.GET)
    public PaginatedResponse<OrderData> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws ApiException {
        return orderDto.getAllOrders(page, size);
    }

    @RequestMapping(path="/get/{orderId}", method = RequestMethod.GET)
    public OrderData getOrderById(@PathVariable Integer orderId) throws ApiException {
        return orderDto.getOrderById(orderId);
    }

    @RequestMapping(path = "/items/{orderId}", method = RequestMethod.GET)
    public List<OrderItemData> getOrderItems(@PathVariable Integer orderId) throws ApiException{
        return orderDto.getOrderItems(orderId);
    }

    @RequestMapping(value = "/status/{status}", method = RequestMethod.GET)
    public List<OrderData> getByStatus(@PathVariable String status) throws ApiException {
        return orderDto.getOrdersByStatus(status);
    }

    @RequestMapping(value = "/update-status/{orderId}/{status}", method = RequestMethod.PUT)
    public void updateStatus(@PathVariable Integer orderId, @PathVariable String status) throws ApiException {
        orderDto.updateStatus(orderId, status);
    }




}
