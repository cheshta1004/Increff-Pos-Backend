package com.increff.pos.controller;

import com.increff.pos.dto.InventoryDto;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.BulkInventoryData;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.form.InventoryForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryDto inventoryDto;
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    public void addInventory(@RequestBody InventoryForm form) throws ApiException {
        inventoryDto.addInventory(form); 
    }

    @RequestMapping(path = "/add-list", method = RequestMethod.POST)
    public BulkInventoryData addInventory(@RequestBody List<InventoryForm> formList) throws ApiException {
        return inventoryDto.addInventoryFromList(formList);
    }

    @RequestMapping(path = "/updateList", method = RequestMethod.PUT)
    public BulkInventoryData updateInventory(@RequestBody List<InventoryForm> formList) {
        return inventoryDto.updateInventoryFromList(formList);
    }

    @RequestMapping(path = "/update/{barcode}", method = RequestMethod.PUT)
    public void updateInventory(@PathVariable String barcode, @RequestBody InventoryForm form) throws ApiException {
        inventoryDto.updateInventory(barcode, form);
    }

    @RequestMapping(path = "/get/{barcode}", method = RequestMethod.GET)
    public InventoryData getInventoryByBarcode(@PathVariable String barcode) throws ApiException {
        return inventoryDto.getInventoryByBarcode(barcode);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<InventoryData> getAllInventory() throws ApiException {
        return inventoryDto.getAll();
    }
    
}
