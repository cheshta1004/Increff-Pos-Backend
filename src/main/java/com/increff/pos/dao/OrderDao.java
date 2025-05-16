package com.increff.pos.dao;

import com.increff.pos.pojo.OrderPojo;
import org.springframework.stereotype.Repository;
import com.increff.pos.model.enums.OrderStatus;
import javax.persistence.criteria.*;
import java.util.List;

@Repository
public class OrderDao extends AbstractDao<OrderPojo>{

    public OrderDao(){
        super(OrderPojo.class);
    }

}
