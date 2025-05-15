package com.increff.pos.dto;

import com.increff.pos.api.DailyReportApi;
import com.increff.pos.dto.helper.DtoHelper;
import com.increff.pos.model.data.DailyReportData;
import com.increff.pos.pojo.DailyReportPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.time.ZonedDateTime;

@Component
public class DailyReportDto {
    @Autowired
    private DailyReportApi dailyReportApi;

    public List<DailyReportData> getAllDailyReports() {
        return DtoHelper.convertToDataList(dailyReportApi.getAllDailyReports());
    }

    public void recalculateDailyReport(ZonedDateTime date) {
        dailyReportApi.recalculateDailyReport(date);
    }

    public List<DailyReportData> getDailyReportsByDateRange(ZonedDateTime startDate, ZonedDateTime endDate) {
        return DtoHelper.convertToDataList(dailyReportApi.getDailyReportsByDateRange(startDate, endDate));
    }
}