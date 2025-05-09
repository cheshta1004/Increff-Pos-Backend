package com.increff.pos.dao;

import com.increff.pos.pojo.InventoryPojo;
import org.springframework.stereotype.Repository;


@Repository
public class InventoryDao extends AbstractDao<InventoryPojo> {
    public InventoryDao(){
        super(InventoryPojo.class);
    }
}
