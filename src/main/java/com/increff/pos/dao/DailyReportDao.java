package com.increff.pos.dao;

import com.increff.pos.pojo.DailyReportPojo;
import org.springframework.stereotype.Repository;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;

@Repository
public class DailyReportDao extends AbstractDao<DailyReportPojo> {

    public DailyReportDao() {
        super(DailyReportPojo.class);
    }

    public DailyReportPojo selectByDate(LocalDate date) {
        TypedQuery<DailyReportPojo> query = getQuery("SELECT p FROM DailyReportPojo p WHERE p.date = :date", DailyReportPojo.class);
        query.setParameter("date", date);
        return getSingle(query);
    }

    public List<DailyReportPojo> selectAll() {
        TypedQuery<DailyReportPojo> query = getQuery("SELECT p FROM DailyReportPojo p ORDER BY p.date DESC", DailyReportPojo.class);
        return query.getResultList();
    }
} 