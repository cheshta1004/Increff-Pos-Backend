package com.increff.pos.model.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopClientsData {
    private Integer clientId;
    private String clientName;
    private Long orderCount;
    private Double totalValue;

    public TopClientsData(Integer clientId, String clientName, Long orderCount, Double totalValue) {
        this.clientId = clientId;
        this.clientName = clientName;
        this.orderCount = orderCount;
        this.totalValue = totalValue;
    }
}
