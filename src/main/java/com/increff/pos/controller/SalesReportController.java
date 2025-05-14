package com.increff.pos.controller;

import com.increff.pos.dto.SalesReportDto;
import com.increff.pos.model.data.SalesReportData;
import com.increff.pos.model.form.SalesReportFilterForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.increff.pos.exception.ApiException;
import java.util.List;

@RestController
@RequestMapping("/api/revenue")
public class SalesReportController {

    @Autowired
    private SalesReportDto salesReportDto;

    @RequestMapping(path = "/filtered-sales-report", method = RequestMethod.POST)
    public List<SalesReportData> getFilteredSalesReport( @RequestBody SalesReportFilterForm form) throws ApiException   {
        return salesReportDto.getFilteredSalesReport(form);
    }
}
