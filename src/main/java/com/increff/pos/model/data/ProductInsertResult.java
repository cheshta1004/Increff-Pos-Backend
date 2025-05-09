package com.increff.pos.model.data;

import com.increff.pos.model.form.ProductForm;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ProductInsertResult {

    private List<OperationResponse<ProductForm>> successList;
    private List<OperationResponse<ProductForm>> failureList;
}
