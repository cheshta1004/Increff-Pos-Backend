package com.increff.pos.dto;

import com.increff.pos.api.DailyReportApi;
import com.increff.pos.model.data.DailyReportData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DailyReportTest {

    @InjectMocks
    private DailyReportDto dailyReportDto;

    @Mock
    private DailyReportApi dailyReportApi;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    private DailyReportData createDailyReportData() {
        DailyReportData data = new DailyReportData();
        data.setDate(LocalDate.of(2025, 5, 7).toString());
        data.setOrderCount(3L);
        data.setTotalItems(15L);
        data.setRevenue(500.0);
        return data;
    }

    @Test
    public void testGetAllDailyReports() {
        List<DailyReportData> expected = Collections.singletonList(createDailyReportData());
        when(dailyReportApi.getAllDailyReports()).thenReturn(expected);

        List<DailyReportData> actual = dailyReportDto.getAllDailyReports();

        assertEquals(expected, actual);
        verify(dailyReportApi, times(1)).getAllDailyReports();
    }
} 