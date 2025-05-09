package com.increff.pos.controller;

import com.increff.pos.dto.RevenueDto;
import com.increff.pos.model.data.RevenueData;
import com.increff.pos.model.form.SalesReportFilterForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/revenue")
public class RevenueController {

    @Autowired
    private RevenueDto revenueDto;

    @RequestMapping(path="/sales-report", method = RequestMethod.GET)
    public List<RevenueData> getMonthlyProductRevenue() {
        return revenueDto.getMonthlyProductRevenue();
    }

    @RequestMapping(path = "/filtered-sales-report", method = RequestMethod.POST)
    public List<RevenueData> getFilteredSalesReport(@Valid @RequestBody SalesReportFilterForm form) {
        return revenueDto.getFilteredSalesReport(form);
    }
}
