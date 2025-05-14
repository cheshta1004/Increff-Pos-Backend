package com.increff.pos.api;

import com.increff.pos.dao.DailyReportDao;
import com.increff.pos.pojo.DailyReportPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.time.ZonedDateTime;

@Service
@Transactional
public class DailyReportApi {
    @Autowired
    private DailyReportDao dailyReportDao;

    public List<DailyReportPojo> getAllDailyReports() {
        List<DailyReportPojo> pojos = dailyReportDao.selectAll();
        return pojos;
    }

    public void recalculateDailyReport(ZonedDateTime date) {
        dailyReportDao.recalculateDailyReport(date);
    }
} 