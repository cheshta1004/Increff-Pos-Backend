package com.increff.pos.api;

import com.increff.pos.dao.DailyReportDao;
import com.increff.pos.pojo.DailyReportPojo;
import com.increff.pos.model.data.DailyReportData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DailyReportApi {

    @Autowired
    private DailyReportDao dailyReportDao;

    @Transactional
    public void add(DailyReportPojo p) {
        dailyReportDao.insert(p);
    }

    @Transactional
    public DailyReportPojo getByDate(LocalDate date) {
        return dailyReportDao.selectByDate(date);
    }

    @Transactional
    public List<DailyReportPojo> getAll() {
        return dailyReportDao.selectAll();
    }

    public List<DailyReportData> getAllDailyReports() {
        List<DailyReportPojo> pojos = dailyReportDao.selectAll();
        return pojos.stream()
            .map(this::convertToData)
            .collect(Collectors.toList());
    }

    private DailyReportData convertToData(DailyReportPojo pojo) {
        DailyReportData data = new DailyReportData();
        data.setDate(pojo.getDate().toString());
        data.setOrderCount(pojo.getOrderCount());
        data.setTotalItems(pojo.getTotalItems());
        data.setRevenue(pojo.getRevenue());
        return data;
    }
} 