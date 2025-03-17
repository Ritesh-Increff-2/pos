package com.increff.pos.api.flow;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.pos.api.InventoryApi;
import com.increff.pos.api.ProductApi;
import com.increff.pos.db.InventoryPojo;
import com.increff.pos.db.ProductPojo;
import com.increff.pos.exception.ApiException;
import com.increff.pos.helper.InventoryHelper;
@Service
public class InventoryFlow {
    @Autowired
    private InventoryApi inventoryApi;

    @Autowired
    private ProductApi productApi;

    @Autowired
    private InventoryHelper inventoryHelper;
    
    public InventoryPojo addFlow(String barcode, int quantity) throws ApiException {
        ProductPojo product = productApi.getByBarcode(barcode);
        InventoryPojo pojo = inventoryHelper.convertToEntity(barcode, quantity, product.getId());
        return inventoryApi.add(pojo);
    }

    public InventoryPojo getByBarcodeFlow(String barcode) throws ApiException {
        ProductPojo product = productApi.getByBarcode(barcode);
        return inventoryApi.getByProductId(product.getId());
    }

}