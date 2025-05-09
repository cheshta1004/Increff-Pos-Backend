package com.increff.pos.model.form;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ProductBulkInsertForm {
    @NotNull(message = "Product list cannot be null")
    private List<ProductForm> productForms;
}
