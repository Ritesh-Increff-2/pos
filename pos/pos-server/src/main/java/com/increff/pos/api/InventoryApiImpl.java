package com.increff.pos.api;

import java.time.ZonedDateTime;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.db.InventoryPojo;
import com.increff.pos.exception.ApiException;
@Service
public class InventoryApiImpl implements InventoryApi {
    @Autowired
    private InventoryDao inventoryDao;
    
    
   @Override
    public InventoryPojo add(InventoryPojo pojo) throws ApiException {
        InventoryPojo existingInventory = inventoryDao.findByProductId(pojo.getProductId());
        if (Objects.nonNull(existingInventory)) {
            existingInventory.setQuantity(pojo.getQuantity());
            existingInventory.setUpdatedAt(ZonedDateTime.now());
            return inventoryDao.save(existingInventory);
        }
        return inventoryDao.save(pojo);
    }

    @Override
    public InventoryPojo getByProductId(String productId) throws ApiException {
        InventoryPojo inventory = inventoryDao.findByProductId(productId);
        if(Objects.isNull(inventory)){
            throw new ApiException("Inventory not found for product ID: " + productId);
        }
        return inventory;
    }

    @Override
    public InventoryPojo getcheckByProductId(String productId) throws ApiException {
        return  inventoryDao.findByProductId(productId);
    }

    @Override
    public void relativeUpdate(InventoryPojo inventoryPojo, int quantity) throws ApiException {
        int netQuantity = inventoryPojo.getQuantity() + quantity;
        if(netQuantity < 0){
            throw new ApiException("Inventory quantity cannot be negative");
        }
        inventoryPojo.setQuantity(netQuantity);
        inventoryDao.save(inventoryPojo);
    }
   
} 
