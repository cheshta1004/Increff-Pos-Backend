package com.increff.pos.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.HashMap;

@Component
public class SalesReport {
    
    @Autowired
    private RestTemplate restTemplate;
    
    private static final String INVOICE_SERVICE_URL = "http://localhost:9003/api/invoice";
    
    public byte[] generateSalesReport(String startDate, String endDate, String clientName) {
        String url = INVOICE_SERVICE_URL + "/sales-report";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("startDate", startDate);
        requestBody.put("endDate", endDate);
        requestBody.put("clientName", clientName);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        return restTemplate.postForObject(url, request, byte[].class);
    }
} 