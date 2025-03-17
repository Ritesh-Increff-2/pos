package com.increff.pos.api;

import com.increff.pos.db.InventoryPojo;
import com.increff.pos.exception.ApiException;

public interface InventoryApi {
    InventoryPojo add(InventoryPojo inventoryPojo) throws ApiException;
    InventoryPojo getByProductId(String productId) throws ApiException;
    InventoryPojo getcheckByProductId(String productId) throws ApiException;
    void relativeUpdate(InventoryPojo inventoryPojo, int quantity) throws ApiException;
} 