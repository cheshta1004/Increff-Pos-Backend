package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class BulkOrderItemForm {
    @NotEmpty(message = "Order items list cannot be empty")
    @Valid
    private List<OrderItemForm> orderItems;

    @NotBlank(message = "Customer name is required")
    @Size(max = 100, message = "Customer name must be at most 100 characters")
    private String customerName;

    @NotBlank(message = "Customer contact is required")
    @Size(max = 15, message = "Customer contact must be at most 15 characters")
    private String customerContact;
}
