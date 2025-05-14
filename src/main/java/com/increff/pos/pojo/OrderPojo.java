package com.increff.pos.pojo;

import java.time.ZonedDateTime;
import javax.persistence.Entity;
import com.increff.pos.model.enums.OrderStatus;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "orders")
public class OrderPojo extends AbstractVersionedPojo {
// todo check for indexing , unique constraint
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private ZonedDateTime time;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.CREATED;

    @Column(nullable = false, length = 100)
    private String customerName;

    @Column(nullable = false, length = 15)
    private String customerContact;

}
