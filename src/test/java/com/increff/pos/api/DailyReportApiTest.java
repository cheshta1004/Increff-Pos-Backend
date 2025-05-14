package com.increff.pos.api;

import com.increff.pos.dao.DailyReportDao;
import com.increff.pos.pojo.DailyReportPojo;
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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-test.xml"})
@WebAppConfiguration
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DailyReportApiTest {

    @InjectMocks
    private DailyReportApi dailyReportApi;

    @Mock
    private DailyReportDao dailyReportDao;

    private DailyReportPojo testDailyReportPojo;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Setup test data
        testDailyReportPojo = new DailyReportPojo();
        testDailyReportPojo.setDate(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")));
        testDailyReportPojo.setOrderCount(10L);
        testDailyReportPojo.setTotalItems(50L);
        testDailyReportPojo.setRevenue(1000.0);
    }

    @Test
    public void testGetAllDailyReports() {
        List<DailyReportPojo> expectedData = Arrays.asList(testDailyReportPojo);
        when(dailyReportDao.selectAll()).thenReturn(expectedData);

        List<DailyReportPojo> result = dailyReportApi.getAllDailyReports();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDailyReportPojo.getDate(), result.get(0).getDate());
        assertEquals(testDailyReportPojo.getOrderCount(), result.get(0).getOrderCount());
        assertEquals(testDailyReportPojo.getTotalItems(), result.get(0).getTotalItems());
        assertEquals(testDailyReportPojo.getRevenue(), result.get(0).getRevenue());
        verify(dailyReportDao, times(1)).selectAll();
    }

    @Test
    public void testGetAllDailyReports_EmptyList() {
        when(dailyReportDao.selectAll()).thenReturn(Arrays.asList());

        List<DailyReportPojo> result = dailyReportApi.getAllDailyReports();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(dailyReportDao, times(1)).selectAll();
    }

    @Test
    public void testRecalculateDailyReport() {
        ZonedDateTime testDate = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        doNothing().when(dailyReportDao).recalculateDailyReport(testDate);

        dailyReportApi.recalculateDailyReport(testDate);

        verify(dailyReportDao, times(1)).recalculateDailyReport(testDate);
    }

    @Test
    public void testRecalculateDailyReport_WithNullDate() {
        doThrow(new IllegalArgumentException("Date cannot be null"))
            .when(dailyReportDao).recalculateDailyReport(null);

        try {
            dailyReportApi.recalculateDailyReport(null);
            fail("Expected IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Date cannot be null", e.getMessage());
        }

        verify(dailyReportDao, times(1)).recalculateDailyReport(null);
    }
} 