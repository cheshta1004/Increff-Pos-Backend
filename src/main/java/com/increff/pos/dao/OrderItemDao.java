package com.increff.pos.dao;

import com.increff.pos.pojo.OrderItemPojo;
import org.springframework.stereotype.Repository;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public class OrderItemDao extends AbstractDao<OrderItemPojo> {
    public OrderItemDao(){
        super(OrderItemPojo.class);
    }

    public List<OrderItemPojo> selectByOrderId(Integer orderId) {
        CriteriaBuilder criteriaBuilder = em().getCriteriaBuilder();
        CriteriaQuery<OrderItemPojo> criteriaQuery = criteriaBuilder.createQuery(OrderItemPojo.class);
        Root<OrderItemPojo> root = criteriaQuery.from(OrderItemPojo.class);
        Predicate orderPredicate = criteriaBuilder.equal(root.get("orderId"), orderId);
        criteriaQuery.where(orderPredicate);
        return em().createQuery(criteriaQuery).getResultList();
    }
}
