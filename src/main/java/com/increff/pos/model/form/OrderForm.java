package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
public class OrderForm {
    @NotEmpty(message = "Order must contain at least one order item")
    @Valid
    private List<OrderItemForm> orderItems;
}
