package com.increff.pos.api;

import com.increff.pos.dao.SalesReportDao;
import com.increff.pos.model.data.SalesReportData;
import com.increff.pos.model.form.SalesReportFilterForm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.DirtiesContext;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-test.xml"})
@WebAppConfiguration
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SalesReportApiTest {

    @InjectMocks
    private SalesReportApi salesReportApi;

    @Mock
    private SalesReportDao salesReportDao;

    private SalesReportData testSalesReportData;
    private SalesReportFilterForm testFilterForm;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Setup test data
        testSalesReportData = new SalesReportData();
        testSalesReportData.setClientName("Test Client");
        testSalesReportData.setProductName("Test Product");
        testSalesReportData.setBarcode("TEST123");
        testSalesReportData.setQuantity(5L);
        testSalesReportData.setRevenue(100.0);

        // Setup test filter form
        testFilterForm = new SalesReportFilterForm();
        testFilterForm.setStartDate(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).minusDays(7));
        testFilterForm.setEndDate(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")));
        testFilterForm.setClientName("Test Client");
    }

    @Test
    public void testGetFilteredSalesReport_WithAllFilters() {
        List<SalesReportData> expectedData = Arrays.asList(testSalesReportData);
        when(salesReportDao.getFilteredSalesReport(testFilterForm)).thenReturn(expectedData);

        List<SalesReportData> result = salesReportApi.getFilteredSalesReport(testFilterForm);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testSalesReportData.getClientName(), result.get(0).getClientName());
        assertEquals(testSalesReportData.getProductName(), result.get(0).getProductName());
        assertEquals(testSalesReportData.getBarcode(), result.get(0).getBarcode());
        assertEquals(testSalesReportData.getQuantity(), result.get(0).getQuantity());
        assertEquals(testSalesReportData.getRevenue(), result.get(0).getRevenue());
        verify(salesReportDao, times(1)).getFilteredSalesReport(testFilterForm);
    }

    @Test
    public void testGetFilteredSalesReport_WithoutClientName() {
        testFilterForm.setClientName(null);
        List<SalesReportData> expectedData = Arrays.asList(testSalesReportData);
        when(salesReportDao.getFilteredSalesReport(testFilterForm)).thenReturn(expectedData);

        List<SalesReportData> result = salesReportApi.getFilteredSalesReport(testFilterForm);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(salesReportDao, times(1)).getFilteredSalesReport(testFilterForm);
    }

    @Test
    public void testGetFilteredSalesReport_EmptyResult() {
        when(salesReportDao.getFilteredSalesReport(testFilterForm)).thenReturn(Collections.emptyList());

        List<SalesReportData> result = salesReportApi.getFilteredSalesReport(testFilterForm);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(salesReportDao, times(1)).getFilteredSalesReport(testFilterForm);
    }

    @Test
    public void testGetFilteredSalesReport_WithInvalidDateRange() {
        testFilterForm.setStartDate(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")));
        testFilterForm.setEndDate(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).minusDays(1)); // End date before start date
        when(salesReportDao.getFilteredSalesReport(testFilterForm)).thenReturn(Collections.emptyList());

        List<SalesReportData> result = salesReportApi.getFilteredSalesReport(testFilterForm);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(salesReportDao, times(1)).getFilteredSalesReport(testFilterForm);
    }

    @Test
    public void testGetFilteredSalesReport_WithMultipleResults() {
        SalesReportData secondData = new SalesReportData();
        secondData.setClientName("Test Client");
        secondData.setProductName("Another Product");
        secondData.setBarcode("TEST456");
        secondData.setQuantity(3L);
        secondData.setRevenue(75.0);

        List<SalesReportData> expectedData = Arrays.asList(testSalesReportData, secondData);
        when(salesReportDao.getFilteredSalesReport(testFilterForm)).thenReturn(expectedData);

        List<SalesReportData> result = salesReportApi.getFilteredSalesReport(testFilterForm);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testSalesReportData.getClientName(), result.get(0).getClientName());
        assertEquals(secondData.getProductName(), result.get(1).getProductName());
        verify(salesReportDao, times(1)).getFilteredSalesReport(testFilterForm);
    }
}