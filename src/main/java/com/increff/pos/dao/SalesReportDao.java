package com.increff.pos.dao;

import com.increff.pos.model.data.SalesReportData;
import com.increff.pos.model.form.SalesReportFilterForm;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import com.increff.pos.model.enums.OrderStatus;
import java.util.Objects;

@Repository
@Transactional
public class SalesReportDao {
    @PersistenceContext
    private EntityManager entityManager;
 
    public List<SalesReportData> getFilteredSalesReport(SalesReportFilterForm form) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<SalesReportData> query = cb.createQuery(SalesReportData.class);

        Root<OrderPojo> order = query.from(OrderPojo.class);
        Root<OrderItemPojo> orderItem = query.from(OrderItemPojo.class);
        Root<ProductPojo> product = query.from(ProductPojo.class);
        Root<ClientPojo> client = query.from(ClientPojo.class);

        Predicate join1 = cb.equal(orderItem.get("orderId"), order.get("id"));
        Predicate join2 = cb.equal(orderItem.get("productId"), product.get("id"));
        Predicate join3 = cb.equal(product.get("clientId"), client.get("id"));

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(join1);
        predicates.add(join2);
        predicates.add(join3);

        // Add filter to only include completed orders
        predicates.add(cb.equal(order.get("status"), OrderStatus.COMPLETED));

        // Use direct date comparison instead of DATE_FORMAT
        predicates.add(cb.greaterThanOrEqualTo(order.get("time"), form.getStartDate()));
        predicates.add(cb.lessThanOrEqualTo(order.get("time"), form.getEndDate()));

        if (!Objects.isNull(form.getClientName()) && !form.getClientName().isEmpty()) {
            predicates.add(cb.equal(client.get("clientName"), form.getClientName()));
        }

        Expression<Long> totalQty = cb.sum(orderItem.get("quantity"));
        Expression<Double> totalRevenue = cb.sum(cb.prod(orderItem.get("quantity"), orderItem.get("sellingPrice")));

        query.select(cb.construct(
                SalesReportData.class,
                client.get("clientName"),
                product.get("name"),
                product.get("barcode"),
                totalQty,
                totalRevenue
        ))
        .where(cb.and(predicates.toArray(new Predicate[0])))
        .groupBy(product.get("id"), client.get("clientName"), product.get("name"), product.get("barcode"))
        .orderBy(cb.desc(totalRevenue));

        return entityManager.createQuery(query).getResultList();
    }
}
