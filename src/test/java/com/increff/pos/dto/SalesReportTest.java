package com.increff.pos.dto;

import com.increff.pos.api.SalesReportApi;
import com.increff.pos.model.data.SalesReportData;
import com.increff.pos.model.form.SalesReportFilterForm;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import com.increff.pos.exception.ApiException;
import java.time.ZonedDateTime;

public class SalesReportTest {

    @InjectMocks
    private SalesReportDto salesReportDto;

    @Mock
    private SalesReportApi salesReportApi;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetFilteredSalesReport_empty() throws ApiException {
        SalesReportFilterForm form = new SalesReportFilterForm();
        form.setStartDate(ZonedDateTime.now().minusDays(7));
        form.setEndDate(ZonedDateTime.now());
        when(salesReportApi.getFilteredSalesReport(form)).thenReturn(Collections.emptyList());
        List<SalesReportData> result = salesReportDto.getFilteredSalesReport(form);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetFilteredSalesReport_nonEmpty() throws ApiException {
        SalesReportFilterForm form = new SalesReportFilterForm();
        form.setStartDate(ZonedDateTime.now().minusDays(7));
        form.setEndDate(ZonedDateTime.now());
        SalesReportData data = new SalesReportData("client1", "product1", "barcode1", 10L, 100.0);
        when(salesReportApi.getFilteredSalesReport(form)).thenReturn(Arrays.asList(data));
        List<SalesReportData> result = salesReportDto.getFilteredSalesReport(form);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("client1", result.get(0).getClientName());
        assertEquals("product1", result.get(0).getProductName());
        assertEquals("barcode1", result.get(0).getBarcode());
        assertEquals(Long.valueOf(10), result.get(0).getQuantity());
        assertEquals(Double.valueOf(100.0), result.get(0).getRevenue());
    }
} 