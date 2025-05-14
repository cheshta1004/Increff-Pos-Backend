package com.increff.pos.api;

import com.increff.pos.dao.OrderDao;
import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.model.enums.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;
@Service
@Transactional
public class OrderApi {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderItemDao orderItemDao;

    public void insertOrder(OrderPojo pojo) {
        orderDao.insert(pojo);
    }

    public void insertOrder(OrderItemPojo pojo) {
        orderItemDao.insert(pojo);
    }

    public void updateStatus(Integer id, OrderStatus status) throws ApiException {
        OrderPojo orderPojo = orderDao.select(id);
        if (Objects.isNull(orderPojo)) {
            throw new ApiException("Order not found");
        }
        if (orderPojo.getStatus() == OrderStatus.COMPLETED || orderPojo.getStatus() == OrderStatus.CANCELLED) {
            throw new ApiException("Cannot change status of a completed or cancelled order");
        }
        
        orderPojo.setStatus(status);
    }

    public List<OrderPojo> getAllOrders() {
        return orderDao.selectAll();
    }

    public List<OrderItemPojo> getOrderItems(Integer orderId) {
        return orderItemDao.selectByOrderId(orderId);
    }

    public List<OrderPojo> getOrdersByStatus(OrderStatus status) {
        return orderDao.selectByStatus(status);
    }

    public OrderPojo getOrderById(Integer orderId) throws ApiException {
        OrderPojo orderPojo = orderDao.select(orderId);
        if (Objects.isNull(orderPojo)) {
            throw new ApiException("Order not found for ID: " + orderId);
        }
        return orderPojo;
    }
}
