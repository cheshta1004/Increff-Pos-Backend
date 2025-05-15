package com.increff.pos.flow;

import com.increff.pos.api.InventoryApi;
import com.increff.pos.api.ProductApi;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class OrderFlowTest {

    @InjectMocks
    private OrderFlow orderFlow;

    @Mock
    private ProductApi productApi;

    @Mock
    private InventoryApi inventoryApi;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    
    @Test
    public void testReduceInventory_success() throws ApiException {
        InventoryPojo inventory = new InventoryPojo();
        inventory.setProductId(1);
        inventory.setQuantity(10);

        when(inventoryApi.getByProductId(1)).thenReturn(inventory);

        orderFlow.reduceInventory(1, 5);
        verify(inventoryApi).updateInventory(1, 5);
    }

    @Test(expected = ApiException.class)
    public void testReduceInventory_insufficient() throws ApiException {
        InventoryPojo inventory = new InventoryPojo();
        inventory.setProductId(1);
        inventory.setQuantity(2);

        when(inventoryApi.getByProductId(1)).thenReturn(inventory);

        orderFlow.reduceInventory(1, 5);
    }

    @Test
    public void testRestoreInventory_success() throws ApiException {
        InventoryPojo inventory = new InventoryPojo();
        inventory.setProductId(1);
        inventory.setQuantity(10);

        when(inventoryApi.getByProductId(1)).thenReturn(inventory);

        orderFlow.restoreInventory(1, 5);
        verify(inventoryApi).updateInventory(1, 15);
    }
}