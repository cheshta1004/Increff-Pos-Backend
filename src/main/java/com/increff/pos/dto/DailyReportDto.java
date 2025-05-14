package com.increff.pos.dto;

import com.increff.pos.api.DailyReportApi;
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
        List<DailyReportPojo> pojos = dailyReportApi.getAllDailyReports();
        return DtoHelper.convertToDataList(pojos);
    }

    public void recalculateDailyReport(ZonedDateTime date) {
        dailyReportApi.recalculateDailyReport(date);
    }
}