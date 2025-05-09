package com.increff.pos.model.data;

import com.increff.pos.model.form.OrderItemForm;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BulkOrderItemData {
    private Integer orderId;
    private List<OperationResponse<OrderItemForm>> successList = new ArrayList<>();
    private List<OperationResponse<OrderItemForm>> failureList = new ArrayList<>();

    public void addSuccess(OrderItemForm form, String message) {
        successList.add(OperationResponse.success(form, message));
    }

    public void addFailure(OrderItemForm form, String message) {
        failureList.add(OperationResponse.failure(form, message));
    }
}
