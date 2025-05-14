package com.increff.pos.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import com.increff.pos.dto.DailyReportDto;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

@Service
@EnableScheduling
public class DailyReportScheduler {
    static {
        try {
            System.out.println("Initializing DailyReportScheduler static fields...");
            System.out.println("Creating logger...");
            logger = LoggerFactory.getLogger(DailyReportScheduler.class);
            System.out.println("Creating formatters...");
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            istFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Kolkata"));
            System.out.println("Setting timezone constants...");
            UTC_ZONE = ZoneId.of("UTC");
            IST_ZONE = ZoneId.of("Asia/Kolkata");
            System.out.println("Setting message constants...");
            SCHEDULER_START_MESSAGE = "=== Starting Daily Report Generation ===";
            SCHEDULER_END_MESSAGE = "=== Completed Daily Report Generation ===";
            System.out.println("DailyReportScheduler static initialization completed successfully");
        } catch (Exception e) {
            System.err.println("Error during DailyReportScheduler static initialization: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private static final Logger logger;
    private static final DateTimeFormatter formatter;
    private static final DateTimeFormatter istFormatter;
    private static final ZoneId UTC_ZONE;
    private static final ZoneId IST_ZONE;
    private static final String SCHEDULER_START_MESSAGE;
    private static final String SCHEDULER_END_MESSAGE;

    @Value("${scheduler.enabled:true}")
    private boolean schedulerEnabled;

    @Autowired
    private Environment environment;

    @Autowired
    private DailyReportDto dailyReportDto;

    @Scheduled(fixedRate = 600000)
    public void generateDailyReport() {
        if (shouldSkipExecution()) {
            return;
        }
        ZonedDateTime startTime = logSchedulerStart();
        try {
            ZonedDateTime currentDate = getCurrentDate();
            generateReportForDate(currentDate);
            logSchedulerCompletion(startTime);
        } catch (Exception e) {
            handleSchedulerError(e);
        }
        logger.info(SCHEDULER_END_MESSAGE);
    }

    private boolean shouldSkipExecution() {
        if (!schedulerEnabled || isTestEnvironment()) {
            logger.debug("Skipping report generation - scheduler disabled or in test environment");
            return true;
        }
        return false;
    }

    private ZonedDateTime logSchedulerStart() {
        logger.info(SCHEDULER_START_MESSAGE);
        ZonedDateTime startTime = ZonedDateTime.now(UTC_ZONE);
        logger.info("Current time in UTC: {}", startTime.format(formatter));
        logger.info("Current time in IST: {}", startTime.withZoneSameInstant(IST_ZONE).format(istFormatter));
        return startTime;
    }

    private ZonedDateTime getCurrentDate() {
        // Get current date in UTC
        ZonedDateTime currentDate = ZonedDateTime.now(UTC_ZONE)
            .toLocalDate()
            .atStartOfDay(UTC_ZONE);
        
        logger.info("Generating report for date: {} (UTC)", currentDate.format(formatter));
        logger.info("Equivalent IST time: {}", currentDate.withZoneSameInstant(IST_ZONE).format(istFormatter));
        
        return currentDate;
    }

    private void generateReportForDate(ZonedDateTime date) {
        dailyReportDto.recalculateDailyReport(date);
        logger.info("Successfully generated daily report for date: {} (UTC)", date.format(formatter));
        logger.info("Equivalent IST time: {}", date.withZoneSameInstant(IST_ZONE).format(istFormatter));
    }

    private void logSchedulerCompletion(ZonedDateTime startTime) {
        ZonedDateTime endTime = ZonedDateTime.now(UTC_ZONE);
        long durationInSeconds = java.time.Duration.between(startTime, endTime).getSeconds();
        logger.info("Daily report generation completed at: {} (UTC)", endTime.format(formatter));
        logger.info("Equivalent IST time: {}", endTime.withZoneSameInstant(IST_ZONE).format(istFormatter));
        logger.info("Time taken: {} seconds", durationInSeconds);
    }

    private void handleSchedulerError(Exception e) {
        logger.error("Error generating daily report: {}", e.getMessage(), e);
        logger.error("Stack trace:", e);
    }

    private boolean isTestEnvironment() {
        return environment.getActiveProfiles().length > 0 &&
            environment.getActiveProfiles()[0].equals("test");
    }
} 