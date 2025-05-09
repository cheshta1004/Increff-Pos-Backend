package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
@Getter
@Setter
public class BulkInventoryForm {
    @NotEmpty(message = "Inventory list cannot be empty")
    @Valid
    private List<InventoryForm> inventoryList;
}
