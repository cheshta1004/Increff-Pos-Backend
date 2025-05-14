package com.increff.pos.dto;

import com.increff.pos.api.SalesReportApi;
import com.increff.pos.model.data.SalesReportData;
import com.increff.pos.model.form.SalesReportFilterForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.increff.pos.util.ValidationUtil;
import com.increff.pos.exception.ApiException;
import com.increff.pos.util.NormalizeUtil;
import java.util.List;

@Component
public class SalesReportDto {

    @Autowired
    private SalesReportApi salesReportApi;

    public List<SalesReportData> getFilteredSalesReport(SalesReportFilterForm form) throws ApiException {
        ValidationUtil.validate(form);
        NormalizeUtil.normalize(form);
        return salesReportApi.getFilteredSalesReport(form);
    }
}
