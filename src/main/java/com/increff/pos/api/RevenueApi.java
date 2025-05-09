package com.increff.pos.api;

import com.increff.pos.dao.RevenueDao;
import com.increff.pos.model.data.DailyReportData;
import com.increff.pos.model.data.RevenueData;
import com.increff.pos.model.form.SalesReportFilterForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class RevenueApi {
    @Autowired
    private RevenueDao revenueDao;

    public List<RevenueData> getMonthlyProductRevenue() {
        return revenueDao.getMonthlyProductRevenue();
    }
    public List<DailyReportData> getAllDailyReports() {
        return revenueDao.getAllDailyReports();
    }

    public List<RevenueData> getFilteredSalesReport(SalesReportFilterForm form) {
        return revenueDao.getFilteredSalesReport(form);
    }
}


