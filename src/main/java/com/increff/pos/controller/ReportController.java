package com.increff.pos.controller;

import com.increff.pos.dto.DailyReportDto;
import com.increff.pos.model.data.DailyReportData;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Api
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private DailyReportDto dailyReportDto;

    @RequestMapping(path = "/daily", method = RequestMethod.GET)
    public List<DailyReportData> getAllDailyReports() {
        return dailyReportDto.getAllDailyReports();
    }
} 