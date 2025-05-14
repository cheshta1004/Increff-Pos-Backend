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
    public List<OrderPojo> selectByStatus(OrderStatus status) {
        CriteriaBuilder cb = em().getCriteriaBuilder();
        CriteriaQuery<OrderPojo> cq = cb.createQuery(OrderPojo.class);
        Root<OrderPojo> root = cq.from(OrderPojo.class);
        cq.select(root).where(cb.equal(root.get("status"), status));
        return em().createQuery(cq).getResultList();
    }
}
