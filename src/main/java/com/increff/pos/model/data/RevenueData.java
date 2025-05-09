package com.increff.pos.model.data;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RevenueData {
    private String clientName;
    private String productName;
    private String barcode;
    private Long quantity;
    private Double revenue;
}
