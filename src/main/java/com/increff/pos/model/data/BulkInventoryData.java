package com.increff.pos.model.data;

import com.increff.pos.model.form.InventoryForm;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class BulkInventoryData {
    private List<OperationResponse<InventoryForm>> successList;
    private List<OperationResponse<InventoryForm>> failureList;
}
