package com.increff.pos.dto;

import com.increff.pos.api.DailyReportApi;
import com.increff.pos.model.data.DailyReportData;
import com.increff.pos.pojo.DailyReportPojo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Collections;
import com.increff.pos.model.form.SalesReportFilterForm;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import com.increff.pos.exception.ApiException;
public class DailyReportTest {

    @InjectMocks
    private DailyReportDto dailyReportDto;

    @Mock
    private DailyReportApi dailyReportApi;

    private SalesReportFilterForm testFilterForm;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    private DailyReportPojo createDailyReportPojo() {
        DailyReportPojo pojo = new DailyReportPojo();
        pojo.setDate(ZonedDateTime.of(2025, 5, 7, 0, 0, 0, 0, ZoneId.systemDefault()));
        pojo.setOrderCount(3L);
        pojo.setTotalItems(15L);
        pojo.setRevenue(500.0);
        return pojo;
    }

    @Test
    public void testGetAllDailyReports() throws ApiException {
        List<DailyReportPojo> expectedData = Collections.singletonList(createDailyReportPojo());
        when(dailyReportApi.getAllDailyReports()).thenReturn(expectedData);

        List<DailyReportData> actual = dailyReportDto.getAllDailyReports();

        assertNotNull(actual);
        assertEquals(1, actual.size());
        verify(dailyReportApi, times(1)).getAllDailyReports();
    }

} 