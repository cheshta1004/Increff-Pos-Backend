package com.increff.pos.flow;

import com.increff.pos.report.SalesReport;
import com.increff.pos.model.form.SalesReportFilterForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.format.DateTimeFormatter;

@Component
public class SalesReportFlow {
    
    @Autowired
    private SalesReport salesReport;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public byte[] generateSalesReport(SalesReportFilterForm form) {
        String startDate = form.getStartDate().format(DATE_FORMATTER);
        String endDate = form.getEndDate().format(DATE_FORMATTER);
        return salesReport.generateSalesReport(startDate, endDate, form.getClientName());
    }
} 