package com.increff.pos.model.form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryForm {
    private String productBarcode;
    @NotNull(message = "Quantity must not be null")
    @PositiveOrZero(message = "Quantity cannot be negative")
    private Integer quantity;
}
