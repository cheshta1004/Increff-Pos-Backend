package com.increff.pos.dto;

import com.increff.pos.flow.SalesReportFlow;
import com.increff.pos.api.SalesReportApi;
import com.increff.pos.model.data.SalesReportData;
import com.increff.pos.model.form.SalesReportFilterForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.increff.pos.util.ValidationUtil;
import com.increff.pos.exception.ApiException;
import com.increff.pos.util.NormalizeUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.util.List;

@Component
public class SalesReportDto {
    @Autowired
    private SalesReportApi salesReportApi;

    @Autowired
    private SalesReportFlow salesReportFlow;

    public List<SalesReportData> getFilteredSalesReport(SalesReportFilterForm form) throws ApiException {
        ValidationUtil.validate(form);
        NormalizeUtil.normalize(form);
        return salesReportApi.getFilteredSalesReport(form);
    }

    public ResponseEntity<byte[]> generateSalesReportResponse(SalesReportFilterForm form) throws ApiException {
        ValidationUtil.validate(form);
        NormalizeUtil.normalize(form);
        byte[] pdfBytes = salesReportFlow.generateSalesReport(form);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "sales-report.pdf");
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
