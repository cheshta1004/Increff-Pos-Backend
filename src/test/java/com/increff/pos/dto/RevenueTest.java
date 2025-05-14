package com.increff.pos.dto;

import com.increff.pos.api.SalesReportApi;
import com.increff.pos.model.data.SalesReportData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RevenueTest {

    @InjectMocks
    private SalesReportDto revenueDto;

    @Mock
    private SalesReportApi revenueApi;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    private SalesReportData createRevenueData() {
        SalesReportData data = new SalesReportData();
        data.setProductName("Test Product");
        data.setRevenue(100.0);
        data.setQuantity(5L);
        data.setClientName("Test Client");
        data.setBarcode("TEST123");
        return data;
    }

   
}
