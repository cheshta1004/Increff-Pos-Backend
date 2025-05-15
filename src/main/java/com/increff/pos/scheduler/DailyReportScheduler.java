package com.increff.pos.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.ZonedDateTime;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import com.increff.pos.dto.DailyReportDto;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

@Service
@EnableScheduling
public class DailyReportScheduler {
   private static final ZoneId UTC_ZONE = ZoneId.of("UTC");

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
        ZonedDateTime currentDate = ZonedDateTime.now(UTC_ZONE)
            .toLocalDate()
            .atStartOfDay(UTC_ZONE);
        dailyReportDto.recalculateDailyReport(currentDate);
    }

    private boolean shouldSkipExecution() {
        return !schedulerEnabled || isTestEnvironment();
    }

    private boolean isTestEnvironment() {
        return environment.getActiveProfiles().length > 0 &&
            environment.getActiveProfiles()[0].equals("test");
    }
} 