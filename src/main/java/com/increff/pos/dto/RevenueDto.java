package com.increff.pos.dto;

import com.increff.pos.api.DailyReportApi;
import com.increff.pos.api.RevenueApi;
import com.increff.pos.model.data.DailyReportData;
import com.increff.pos.model.data.RevenueData;
import com.increff.pos.model.form.SalesReportFilterForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class RevenueDto {

    @Autowired
    private RevenueApi revenueApi;


    public List<RevenueData> getMonthlyProductRevenue() {
        return revenueApi.getMonthlyProductRevenue();
    }

    public List<RevenueData> getFilteredSalesReport(SalesReportFilterForm form) {
        return revenueApi.getFilteredSalesReport(form);
    }
}
