package com.increff.pos.api;

import com.increff.pos.dao.OrderDao;
import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.model.enums.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class OrderApi {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    private ProductApi productApi;

    @Autowired
    private InventoryApi inventoryApi;


    public void insertOrder(OrderPojo pojo) {
        orderDao.insert(pojo);
    }

    public void insertOrder(OrderItemPojo pojo) {
        orderItemDao.insert(pojo);
    }

    public void updateStatus(Integer id, OrderStatus status) {
        OrderPojo orderPojo = orderDao.select(id);
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


    public OrderPojo getOrderById(Integer orderId) {
        return orderDao.select(orderId);
    }
}
