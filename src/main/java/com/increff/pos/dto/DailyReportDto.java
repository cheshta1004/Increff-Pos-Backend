package com.increff.pos.dto;

import com.increff.pos.api.DailyReportApi;
import com.increff.pos.dto.helper.DtoHelper;
import com.increff.pos.model.data.DailyReportData;
import com.increff.pos.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.time.ZonedDateTime;

@Component
public class DailyReportDto {
    @Autowired
    private DailyReportApi dailyReportApi;

    public List<DailyReportData> getAllDailyReports() throws ApiException {
        return DtoHelper.convertDailyReportPojoListToData(dailyReportApi.getAllDailyReports());
    }

    public void recalculateDailyReport(ZonedDateTime date) {
        dailyReportApi.recalculateDailyReport(date);
    }

    public List<DailyReportData> getDailyReportsByDateRange(String startDate, String endDate) throws ApiException {
        return DtoHelper.convertDailyReportPojoListToData(
            dailyReportApi.getDailyReportsByDateRange(
                DtoHelper.getDateRangeStart(startDate),
                DtoHelper.getDateRangeEnd(endDate)
            )
        );
    }

  
}