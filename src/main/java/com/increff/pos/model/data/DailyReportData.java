package com.increff.pos.model.data;

import lombok.Getter;
import lombok.Setter;
import java.time.ZonedDateTime;

@Getter
@Setter
public class DailyReportData {
    private Integer id;
    private ZonedDateTime date;
    private Long orderCount;
    private Long totalItems;
    private Double revenue;
}
