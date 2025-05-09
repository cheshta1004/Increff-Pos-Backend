package com.increff.pos.dto;

import com.increff.pos.api.RevenueApi;
import com.increff.pos.model.data.RevenueData;
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
    private RevenueDto revenueDto;

    @Mock
    private RevenueApi revenueApi;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    private RevenueData createRevenueData() {
        RevenueData data = new RevenueData();
        data.setProductName("Test Product");
        data.setRevenue(100.0);
        data.setQuantity(5L);
        data.setClientName("Test Client");
        data.setBarcode("TEST123");
        return data;
    }

    @Test
    public void testGetMonthlyProductRevenue() {
        List<RevenueData> expected = Collections.singletonList(createRevenueData());
        when(revenueApi.getMonthlyProductRevenue()).thenReturn(expected);

        List<RevenueData> actual = revenueDto.getMonthlyProductRevenue();

        assertEquals(expected, actual);
        verify(revenueApi, times(1)).getMonthlyProductRevenue();
    }
}
