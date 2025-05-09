package com.increff.pos.model.data;

import lombok.Getter;
import lombok.Setter;
import com.increff.pos.model.enums.OrderStatus;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
public class OrderData {

    private Integer id;
    private ZonedDateTime time;
    private List<OrderItemData> items;
    private OrderStatus status;
    private String customerName;
    private String customerContact;

}
