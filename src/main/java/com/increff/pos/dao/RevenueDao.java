package com.increff.pos.dao;

import com.increff.pos.model.data.DailyReportData;
import com.increff.pos.model.data.RevenueData;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class RevenueDao {
    @PersistenceContext
    private EntityManager entityManager;

    public List<RevenueData> getMonthlyProductRevenue() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<RevenueData> query = cb.createQuery(RevenueData.class);

        Root<OrderPojo> order = query.from(OrderPojo.class);
        Root<OrderItemPojo> orderItem = query.from(OrderItemPojo.class);
        Root<ProductPojo> product = query.from(ProductPojo.class);
        Root<ClientPojo> client = query.from(ClientPojo.class);

        // Join predicates
        Predicate join1 = cb.equal(orderItem.get("orderId"), order.get("id"));
        Predicate join2 = cb.equal(orderItem.get("productId"), product.get("id"));
        Predicate join3 = cb.equal(product.get("clientId"), client.get("id"));

        // Date filter: orders in last 1 month
        ZonedDateTime oneMonthAgo = ZonedDateTime.now(ZoneId.of("UTC")).minusMonths(1);
        Predicate dateFilter = cb.greaterThanOrEqualTo(order.get("time"), oneMonthAgo);

        // Expressions
        Expression<Long> totalQty = cb.sum(orderItem.get("quantity"));
        Expression<Double> totalRevenue = cb.sum(cb.prod(orderItem.get("quantity"), orderItem.get("sellingPrice")));

        query.select(cb.construct(
                        RevenueData.class,
                        client.get("clientName"),
                        product.get("name"),
                        product.get("barcode"),
                        totalQty,
                        totalRevenue
                ))
                .where(cb.and(join1, join2, join3, dateFilter))
                .groupBy(product.get("id"), client.get("clientName"), product.get("name"), product.get("barcode"))
                .orderBy(cb.desc(totalRevenue));

        return entityManager.createQuery(query).getResultList();
    }

    public List<DailyReportData> getAllDailyReports() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DailyReportData> query = cb.createQuery(DailyReportData.class);

        Root<OrderPojo> order = query.from(OrderPojo.class);
        Root<OrderItemPojo> orderItem = query.from(OrderItemPojo.class);

        Predicate joinCondition = cb.equal(order.get("id"), orderItem.get("orderId"));

        Expression<String> dateFormatted = cb.function("DATE_FORMAT", String.class, order.get("time"), cb.literal("%Y-%m-%d"));
        Expression<Long> itemCount = cb.sum(orderItem.get("quantity"));
        Expression<Double> totalRevenue = cb.sum(cb.prod(orderItem.get("quantity"), orderItem.get("sellingPrice")));
        Expression<Long> orderCount = cb.countDistinct(order.get("id"));

        query.select(cb.construct(
                        DailyReportData.class,
                        dateFormatted,
                        orderCount,
                        itemCount,
                        totalRevenue
                ))
                .where(joinCondition)
                .groupBy(dateFormatted)
                .orderBy(cb.asc(dateFormatted));

        return entityManager.createQuery(query).getResultList();
    }

    public List<RevenueData> getFilteredSalesReport(SalesReportFilterForm form) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<RevenueData> query = cb.createQuery(RevenueData.class);

        Root<OrderPojo> order = query.from(OrderPojo.class);
        Root<OrderItemPojo> orderItem = query.from(OrderItemPojo.class);
        Root<ProductPojo> product = query.from(ProductPojo.class);
        Root<ClientPojo> client = query.from(ClientPojo.class);

        // Join predicates
        Predicate join1 = cb.equal(orderItem.get("orderId"), order.get("id"));
        Predicate join2 = cb.equal(orderItem.get("productId"), product.get("id"));
        Predicate join3 = cb.equal(product.get("clientId"), client.get("id"));

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(join1);
        predicates.add(join2);
        predicates.add(join3);

        // Date range filter
        Expression<String> dateFormatted = cb.function("DATE_FORMAT", String.class, order.get("time"), cb.literal("%Y-%m-%d"));
        predicates.add(cb.greaterThanOrEqualTo(dateFormatted, form.getStartDate().toString()));
        predicates.add(cb.lessThanOrEqualTo(dateFormatted, form.getEndDate().toString()));

        // Client name filter
        if (form.getClientName() != null && !form.getClientName().isEmpty()) {
            predicates.add(cb.equal(client.get("clientName"), form.getClientName()));
        }

        // Expressions
        Expression<Long> totalQty = cb.sum(orderItem.get("quantity"));
        Expression<Double> totalRevenue = cb.sum(cb.prod(orderItem.get("quantity"), orderItem.get("sellingPrice")));

        query.select(cb.construct(
                RevenueData.class,
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
