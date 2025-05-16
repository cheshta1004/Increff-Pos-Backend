package com.increff.pos.api;

import com.increff.pos.dao.SalesReportDao;
import com.increff.pos.model.data.SalesReportData;
import com.increff.pos.model.form.SalesReportFilterForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class SalesReportApi {
    @Autowired
    private SalesReportDao salesReportDao;

    public List<SalesReportData> getFilteredSalesReport(SalesReportFilterForm form) {
        return salesReportDao.getFilteredSalesReport(form);
    }
}

