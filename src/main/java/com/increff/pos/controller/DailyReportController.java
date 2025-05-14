package com.increff.pos.controller;

import com.increff.pos.dto.DailyReportDto;
import com.increff.pos.model.data.DailyReportData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/api/reports")
public class DailyReportController {
    @Autowired
    private DailyReportDto dailyReportDto;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @RequestMapping(path = "/daily", method = RequestMethod.GET)
    public List<DailyReportData> getAllDailyReports() {
        return dailyReportDto.getAllDailyReports();
    }

    @RequestMapping(path = "/daily/recalculate", method = RequestMethod.POST)
    @ResponseBody
    public String recalculateDailyReport() { 
        ZonedDateTime currentDate = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        dailyReportDto.recalculateDailyReport(currentDate);
        return "Daily report recalculated successfully for date: " + currentDate.format(formatter);
    }

    @RequestMapping(path = "/daily/recalculate-all", method = RequestMethod.POST)
    @ResponseBody
    public String recalculateAllReports() {
        ZonedDateTime currentDate = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        for (int i = 0; i < 30; i++) {
            ZonedDateTime date = currentDate.minus(i, ChronoUnit.DAYS);
            dailyReportDto.recalculateDailyReport(date);
        }
        return "Daily reports recalculated successfully for the last 30 days";
    }
} 