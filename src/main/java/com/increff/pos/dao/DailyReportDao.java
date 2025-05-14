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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.persistence.NoResultException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Repository
@Transactional
public class DailyReportDao extends AbstractDao<DailyReportPojo> {
    static {
        try {
            System.out.println("Initializing DailyReportDao static fields...");
            System.out.println("Creating logger...");
            logger = LoggerFactory.getLogger(DailyReportDao.class);
            System.out.println("Creating formatters...");
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            istFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Kolkata"));
            System.out.println("Setting timezone constants...");
            UTC_ZONE = ZoneId.of("UTC");
            IST_ZONE = ZoneId.of("Asia/Kolkata");
            System.out.println("DailyReportDao static initialization completed successfully");
        } catch (Exception e) {
            System.err.println("Error during DailyReportDao static initialization: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private static final Logger logger;
    private static final DateTimeFormatter formatter;
    private static final DateTimeFormatter istFormatter;
    private static final ZoneId UTC_ZONE;
    private static final ZoneId IST_ZONE;

    public DailyReportDao() {
        super(DailyReportPojo.class);
    }

    public DailyReportPojo calculateDailyReport(ZonedDateTime date) {
        logger.info("Calculating report for date: {} (UTC)", date.format(formatter));
        logger.info("Equivalent IST time: {}", date.withZoneSameInstant(IST_ZONE).format(istFormatter));
            
        // Convert to UTC timezone for start/end of day
        ZonedDateTime startOfDay = date.toLocalDate().atStartOfDay(UTC_ZONE);
        ZonedDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
        
        logger.info("Searching for orders between {} and {} (UTC)", 
            startOfDay.format(formatter), 
            endOfDay.format(formatter));
        logger.info("Equivalent IST times: {} to {}", 
            startOfDay.withZoneSameInstant(IST_ZONE).format(istFormatter),
            endOfDay.withZoneSameInstant(IST_ZONE).format(istFormatter));
        
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
        logger.info("Found {} orders for date: {}", orders.size(), date.toLocalDate());
        
        // Log details of each order found
        for (OrderPojo order : orders) {
            logger.info("Order ID: {}, DateTime: {} (UTC), {} (IST), Status: {}", 
                order.getId(),
                order.getTime().format(formatter),
                order.getTime().withZoneSameInstant(IST_ZONE).format(istFormatter),
                order.getStatus());
        }
        
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
            logger.info("Order ID: {} has {} items", order.getId(), items.size());
            
            for (OrderItemPojo item : items) {
                totalItems += item.getQuantity();
                totalRevenue += item.getQuantity() * item.getSellingPrice();
                logger.info("Order ID: {}, Item: {}, Quantity: {}, Price: {}, Subtotal: {}", 
                    order.getId(), 
                    item.getId(),
                    item.getQuantity(),
                    item.getSellingPrice(),
                    item.getQuantity() * item.getSellingPrice());
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
        
        logger.info("Calculated report for date: {} (UTC) - Orders: {}, Items: {}, Revenue: {}", 
            date.format(formatter), totalOrders, totalItems, totalRevenue);
        logger.info("Equivalent IST time: {}", date.withZoneSameInstant(IST_ZONE).format(istFormatter));
            
        return report;
    }
  
    public DailyReportPojo selectByDate(ZonedDateTime date) {
        if (Objects.isNull(date)) {
            logger.error("Date parameter is null in selectByDate");
            throw new IllegalArgumentException("Date cannot be null");
        }

        logger.info("Selecting report for date: {} (UTC)", date.format(formatter));
        logger.info("Equivalent IST time: {}", date.withZoneSameInstant(IST_ZONE).format(istFormatter));
            
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DailyReportPojo> cq = cb.createQuery(DailyReportPojo.class);
        Root<DailyReportPojo> root = cq.from(DailyReportPojo.class);
        
        // Convert to start of day in UTC timezone
        ZonedDateTime startOfDay = date.toLocalDate().atStartOfDay(UTC_ZONE);
        
        cq.select(root)
        .where(cb.equal(root.get("date"), startOfDay));
        
        try {
            DailyReportPojo result = getSingle(em.createQuery(cq));
            if (Objects.nonNull(result)) {
                logger.info("Found existing report for date: {} (UTC) with ID: {}", 
                    date.format(formatter), result.getId());
                logger.info("Equivalent IST time: {}", 
                    date.withZoneSameInstant(IST_ZONE).format(istFormatter));
            }
            return result;
        } catch (NoResultException e) {
            logger.info("No existing report found for date: {} (UTC)", date.format(formatter));
            logger.info("Equivalent IST time: {}", date.withZoneSameInstant(IST_ZONE).format(istFormatter));
            return null;
        }
    }

    public List<DailyReportPojo> selectAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DailyReportPojo> cq = cb.createQuery(DailyReportPojo.class);
        Root<DailyReportPojo> root = cq.from(DailyReportPojo.class);
        
        cq.select(root)
        .orderBy(cb.desc(root.get("date")));
        
        List<DailyReportPojo> results = em.createQuery(cq).getResultList();
        logger.info("Found {} daily reports in total", results.size());
        return results;
    }

    public void recalculateDailyReport(ZonedDateTime date) {
        logger.info("Recalculating daily report for date: {} (UTC)", date.format(formatter));
        logger.info("Equivalent IST time: {}", date.withZoneSameInstant(IST_ZONE).format(istFormatter));
        
        // First delete any existing report for this date
        DailyReportPojo existingReport = selectByDate(date);
        if (!Objects.isNull(existingReport)) {
            logger.info("Deleting existing report with ID: {}", existingReport.getId());
            delete(existingReport.getId());
        }
        
        // Create new report
        logger.info("Creating new report for date: {} (UTC)", date.format(formatter));
        logger.info("Equivalent IST time: {}", date.withZoneSameInstant(IST_ZONE).format(istFormatter));
        DailyReportPojo newPojo = calculateDailyReport(date);
        insert(newPojo);
        logger.info("Successfully created new report with ID: {}", newPojo.getId());
    }
}