package com.increff.pos.api;

import com.increff.pos.dao.RevenueDao;
import com.increff.pos.model.data.DailyReportData;
import com.increff.pos.model.data.RevenueData;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-test.xml"})
@WebAppConfiguration
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RevenueApiTest {

    @InjectMocks
    private RevenueApi revenueApi;

    @Mock
    private RevenueDao revenueDao;

    private RevenueData testRevenueData;
    private DailyReportData testDailyReportData;
    private SalesReportFilterForm testFilterForm;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);

        testRevenueData = new RevenueData();
        testRevenueData.setClientName("Test Client");
        testRevenueData.setProductName("Test Product");
        testRevenueData.setBarcode("TEST123");
        testRevenueData.setQuantity(5L);
        testRevenueData.setRevenue(100.0);

        testDailyReportData = new DailyReportData();
        testDailyReportData.setDate(LocalDate.now().toString());
        testDailyReportData.setOrderCount(10L);
        testDailyReportData.setTotalItems(50L);
        testDailyReportData.setRevenue(1000.0);

        testFilterForm = new SalesReportFilterForm();
        testFilterForm.setStartDate(LocalDate.now().minusDays(7));
        testFilterForm.setEndDate(LocalDate.now());
        testFilterForm.setClientName("Test Client");
    }

    // Test to verify that the monthly product revenue data is fetched and returned correctly
    @Test
    public void testGetMonthlyProductRevenue() {
        List<RevenueData> expectedData = Arrays.asList(testRevenueData);
        when(revenueDao.getMonthlyProductRevenue()).thenReturn(expectedData);

        List<RevenueData> result = revenueApi.getMonthlyProductRevenue();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testRevenueData.getClientName(), result.get(0).getClientName());
        assertEquals(testRevenueData.getProductName(), result.get(0).getProductName());
        assertEquals(testRevenueData.getBarcode(), result.get(0).getBarcode());
        assertEquals(testRevenueData.getQuantity(), result.get(0).getQuantity());
        assertEquals(testRevenueData.getRevenue(), result.get(0).getRevenue());
        verify(revenueDao, times(1)).getMonthlyProductRevenue();
    }

    // Test to verify that all daily reports are fetched and returned correctly
    @Test
    public void testGetAllDailyReports() {
        List<DailyReportData> expectedData = Arrays.asList(testDailyReportData);
        when(revenueDao.getAllDailyReports()).thenReturn(expectedData);

        List<DailyReportData> result = revenueApi.getAllDailyReports();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDailyReportData.getDate(), result.get(0).getDate());
        assertEquals(testDailyReportData.getOrderCount(), result.get(0).getOrderCount());
        assertEquals(testDailyReportData.getTotalItems(), result.get(0).getTotalItems());
        assertEquals(testDailyReportData.getRevenue(), result.get(0).getRevenue());
        verify(revenueDao, times(1)).getAllDailyReports();
    }

    // Test to verify that the filtered sales report data is fetched and returned correctly based on the filter form
    @Test
    public void testGetFilteredSalesReport() {
        List<RevenueData> expectedData = Arrays.asList(testRevenueData);
        when(revenueDao.getFilteredSalesReport(testFilterForm)).thenReturn(expectedData);

        List<RevenueData> result = revenueApi.getFilteredSalesReport(testFilterForm);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testRevenueData.getClientName(), result.get(0).getClientName());
        assertEquals(testRevenueData.getProductName(), result.get(0).getProductName());
        assertEquals(testRevenueData.getBarcode(), result.get(0).getBarcode());
        assertEquals(testRevenueData.getQuantity(), result.get(0).getQuantity());
        assertEquals(testRevenueData.getRevenue(), result.get(0).getRevenue());
        verify(revenueDao, times(1)).getFilteredSalesReport(testFilterForm);
    }

    // Test to verify that the filtered sales report can be fetched without the client name filter
    @Test
    public void testGetFilteredSalesReport_withoutClientName() {
        testFilterForm.setClientName(null);
        List<RevenueData> expectedData = Arrays.asList(testRevenueData);
        when(revenueDao.getFilteredSalesReport(testFilterForm)).thenReturn(expectedData);

        List<RevenueData> result = revenueApi.getFilteredSalesReport(testFilterForm);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(revenueDao, times(1)).getFilteredSalesReport(testFilterForm);
    }
} 