package com.increff.pos.scheduler;

import com.increff.pos.api.DailyReportApi;
import com.increff.pos.api.RevenueApi;
import com.increff.pos.model.data.DailyReportData;
import com.increff.pos.pojo.DailyReportPojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;

@Component
public class DailyReportScheduler {
    private static final Logger logger = LoggerFactory.getLogger(DailyReportScheduler.class);

    @Autowired
    private RevenueApi revenueApi;

    @Autowired
    private DailyReportApi dailyReportApi;

    @Scheduled(fixedRate = 600000) // Run every 10 minutes
    public void generateDailyReport() {
        try {
            logger.info("=== Scheduler started at: {} ===", java.time.LocalDateTime.now());
            List<DailyReportData> reports = revenueApi.getAllDailyReports();
            logger.info("Fetched {} reports from RevenueApi", reports.size());
            
            for (DailyReportData report : reports) {
                LocalDate reportDate = LocalDate.parse(report.getDate());
                logger.info("Processing report for date: {} with data - Orders: {}, Items: {}, Revenue: {}", 
                    report.getDate(),
                    report.getOrderCount(), 
                    report.getTotalItems(), 
                    report.getRevenue());
                
                DailyReportPojo existingReport = dailyReportApi.getByDate(reportDate);
                if (existingReport == null) {
                    DailyReportPojo pojo = new DailyReportPojo();
                    pojo.setDate(reportDate);
                    pojo.setOrderCount(report.getOrderCount());
                    pojo.setTotalItems(report.getTotalItems());
                    pojo.setRevenue(report.getRevenue());
                    dailyReportApi.add(pojo);
                    logger.info("Daily report saved successfully for date: {}", reportDate);
                } else {
                    logger.info("Report already exists for date: {}", reportDate);
                }
            }
            logger.info("=== Scheduler completed at: {} ===", java.time.LocalDateTime.now());
        } catch (Exception e) {
            logger.error("Error in scheduler: " + e.getMessage(), e);
        }
    }
} 