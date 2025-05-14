package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@Table(name = "daily_report")
public class DailyReportPojo extends AbstractVersionedPojo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private ZonedDateTime date;

    @Column(nullable = false)
    private Long orderCount;

    @Column(nullable = false)
    private Long totalItems;

    @Column(nullable = false)
    private Double revenue;
} 