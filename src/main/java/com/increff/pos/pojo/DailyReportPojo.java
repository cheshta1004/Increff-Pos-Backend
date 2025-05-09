package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "daily_report")
@Getter
@Setter
public class DailyReportPojo extends AbstractVersionedPojo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private LocalDate date;

    @Column(nullable = false)
    private Long orderCount;

    @Column(nullable = false)
    private Long totalItems;

    @Column(nullable = false)
    private Double revenue;
} 