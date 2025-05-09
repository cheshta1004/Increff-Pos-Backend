package com.increff.pos.dto;

import com.increff.pos.api.DailyReportApi;
import com.increff.pos.model.data.DailyReportData;
import com.increff.pos.pojo.DailyReportPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DailyReportDto {

    @Autowired
    private DailyReportApi dailyReportApi;

    public List<DailyReportData> getAllDailyReports() {
        return convertToDataList(dailyReportApi.getAll());
    }

    private List<DailyReportData> convertToDataList(List<DailyReportPojo> pojos) {
        return pojos.stream()
            .map(this::convertToData)
            .collect(Collectors.toList());
    }

    private DailyReportData convertToData(DailyReportPojo pojo) {
        return new DailyReportData(
            pojo.getDate().toString(),
            pojo.getOrderCount(),
            pojo.getTotalItems(),
            pojo.getRevenue()
        );
    }
} 