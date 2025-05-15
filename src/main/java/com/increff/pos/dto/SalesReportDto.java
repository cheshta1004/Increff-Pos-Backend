package com.increff.pos.dto;

import com.increff.pos.api.SalesReportApi;
import com.increff.pos.model.data.SalesReportData;
import com.increff.pos.model.form.SalesReportFilterForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.increff.pos.util.ValidationUtil;
import com.increff.pos.exception.ApiException;
import com.increff.pos.util.NormalizeUtil;
import java.time.format.DateTimeFormatter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

@Component
public class SalesReportDto {

    @Autowired
    private SalesReportApi salesReportApi;

    @Autowired
    private RestTemplate restTemplate;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public List<SalesReportData> getFilteredSalesReport(SalesReportFilterForm form) throws ApiException {
        ValidationUtil.validate(form);
        NormalizeUtil.normalize(form);
        return salesReportApi.getFilteredSalesReport(form);
    }
    
    public byte[] generateSalesReport(SalesReportFilterForm form) {
        String url = "http://localhost:9003/api/invoice/sales-report";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("startDate", form.getStartDate().format(DATE_FORMATTER));
        requestBody.put("endDate", form.getEndDate().format(DATE_FORMATTER));
        requestBody.put("clientName", form.getClientName());
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        return restTemplate.postForObject(url, request, byte[].class);
    }
}
