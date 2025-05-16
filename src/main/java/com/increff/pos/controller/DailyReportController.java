package com.increff.pos.controller;

import com.increff.pos.dto.DailyReportDto;
import com.increff.pos.model.data.DailyReportData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.increff.pos.exception.ApiException;

@RestController
@RequestMapping("/api/reports")
public class DailyReportController {
    @Autowired
    private DailyReportDto dailyReportDto;

    @RequestMapping(path = "/daily", method = RequestMethod.GET)
    public List<DailyReportData> getAllDailyReports() throws ApiException {
        return dailyReportDto.getAllDailyReports();
    }


    @RequestMapping(path = "/daily/range", method = RequestMethod.GET)
    public List<DailyReportData> getDailyReportsByDateRange(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) throws ApiException {
        return dailyReportDto.getDailyReportsByDateRange(startDate, endDate);
    }
} 