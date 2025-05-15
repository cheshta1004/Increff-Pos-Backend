package com.increff.pos.dao;

import com.increff.pos.pojo.DailyReportPojo;
import org.springframework.stereotype.Repository;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.ZonedDateTime;
import java.util.List;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.OrderItemPojo;
import org.springframework.transaction.annotation.Transactional;
import com.increff.pos.model.enums.OrderStatus;
import javax.persistence.NoResultException;
import java.time.ZoneId;
import java.util.Objects;

@Repository
@Transactional
public class DailyReportDao extends AbstractDao<DailyReportPojo> {
    private static final ZoneId UTC_ZONE = ZoneId.of("UTC");

    public DailyReportDao() {
        super(DailyReportPojo.class);
    }

    public DailyReportPojo calculateDailyReport(ZonedDateTime date) {
        // Convert to UTC timezone for start/end of day
        ZonedDateTime startOfDay = date.toLocalDate().atStartOfDay(UTC_ZONE);
        ZonedDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
        
        // Get all orders for the day
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<OrderPojo> cq = cb.createQuery(OrderPojo.class);
        Root<OrderPojo> root = cq.from(OrderPojo.class);
        
        cq.select(root)
        .where(cb.and(
            cb.greaterThanOrEqualTo(root.get("time"), startOfDay),
            cb.lessThan(root.get("time"), startOfDay.plusDays(1)),
            cb.equal(root.get("status"), OrderStatus.COMPLETED)
        ));
        
        List<OrderPojo> orders = em.createQuery(cq).getResultList();
        
        // Calculate totals
        long totalOrders = orders.size();
        long totalItems = 0;
        double totalRevenue = 0.0;
        
        for (OrderPojo order : orders) {
            // Get order items
            CriteriaQuery<OrderItemPojo> itemCq = cb.createQuery(OrderItemPojo.class);
            Root<OrderItemPojo> itemRoot = itemCq.from(OrderItemPojo.class);
            
            itemCq.select(itemRoot)
                .where(cb.equal(itemRoot.get("orderId"), order.getId()));
            
            List<OrderItemPojo> items = em.createQuery(itemCq).getResultList();
            
            for (OrderItemPojo item : items) {
                totalItems += item.getQuantity();
                totalRevenue += item.getQuantity() * item.getSellingPrice();
            }
        }
        
        // Create or update report
        DailyReportPojo report = selectByDate(date);
        if (Objects.isNull(report)) {
            report = new DailyReportPojo();
            report.setDate(startOfDay);
        }
        
        report.setOrderCount(totalOrders);
        report.setTotalItems(totalItems);
        report.setRevenue(totalRevenue);
            
        return report;
    }
  
    public DailyReportPojo selectByDate(ZonedDateTime date) {
        if (Objects.isNull(date)) {
            throw new IllegalArgumentException("Date cannot be null");
        }
            
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DailyReportPojo> cq = cb.createQuery(DailyReportPojo.class);
        Root<DailyReportPojo> root = cq.from(DailyReportPojo.class);
        
        // Convert to start of day in UTC timezone
        ZonedDateTime startOfDay = date.toLocalDate().atStartOfDay(UTC_ZONE);
        
        cq.select(root)
        .where(cb.equal(root.get("date"), startOfDay));
        
        try {
            return getSingle(em.createQuery(cq));
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<DailyReportPojo> selectAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DailyReportPojo> cq = cb.createQuery(DailyReportPojo.class);
        Root<DailyReportPojo> root = cq.from(DailyReportPojo.class);
        
        cq.select(root)
        .orderBy(cb.desc(root.get("date")));
        
        return em.createQuery(cq).getResultList();
    }

    public List<DailyReportPojo> selectByDateRange(ZonedDateTime startDate, ZonedDateTime endDate) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DailyReportPojo> cq = cb.createQuery(DailyReportPojo.class);
        Root<DailyReportPojo> root = cq.from(DailyReportPojo.class);
        
        cq.select(root)
          .where(cb.and(
              cb.greaterThanOrEqualTo(root.get("date"), startDate),
              cb.lessThanOrEqualTo(root.get("date"), endDate)
          ))
          .orderBy(cb.desc(root.get("date")));
        
        return em.createQuery(cq).getResultList();
    }

    public void recalculateDailyReport(ZonedDateTime date) {
        ZonedDateTime startOfDay = date.toLocalDate().atStartOfDay(UTC_ZONE);
        DailyReportPojo existingReport = selectByDate(date);
        if (!Objects.isNull(existingReport)) {
            delete(existingReport.getId());
            em.flush();
            em.clear();
            DailyReportPojo verifyDelete = selectByDate(date);
            if (!Objects.isNull(verifyDelete)) {
                throw new IllegalStateException("Failed to delete existing report");
            }
        }
        DailyReportPojo newPojo = calculateDailyReport(date);
        newPojo.setDate(startOfDay);

        DailyReportPojo finalCheck = selectByDate(date);
        if (!Objects.isNull(finalCheck)) {
            throw new IllegalStateException("A report still exists after cleanup");
        }
        insert(newPojo);
        em.flush();
        DailyReportPojo verifyInsert = selectByDate(date);
        if (Objects.isNull(verifyInsert)) {
            throw new IllegalStateException("Failed to insert new report");
        }
    }
}