package com.increff.pos.model.form;

import javax.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductForm {

    @NotBlank(message = "Product barcode must not be blank")
    @Size(max = 20, message = "Product barcode must not exceed 20 characters")
    private String barcode;

    @NotBlank(message = "Client name must not be blank")
    @Size(max = 100, message = "Client name must not exceed 100 characters")
    private String clientName;

    @NotBlank(message = "Product name must not be blank")
    @Size(max = 50, message = "Product name must not exceed 50 characters")
    private String name;

    @NotNull(message = "MRP must not be null")
    @PositiveOrZero(message = "MRP must be greater than or equal to 0")
    @Digits(integer = 10, fraction = 2, message = "MRP must be a valid number with up to 2 decimal places")
    private Double mrp;

    @Size(max = 1000, message = "Product image URL must not exceed 1000 characters")
    private String imageUrl;

}

