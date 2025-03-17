package com.increff.pos.api.flow;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.increff.pos.api.InventoryApi;
import com.increff.pos.api.OrderApi;
import com.increff.pos.api.ProductApi;
import com.increff.pos.db.InventoryPojo;
import com.increff.pos.db.OrderItemPojo;
import com.increff.pos.db.OrderPojo;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.enums.OrderStatus;
@Service
public class OrderFlow {
    @Autowired
    private OrderApi orderApi;
    @Autowired
    private ProductApi productApi;
    @Autowired
    private InventoryApi inventoryApi;


    @Transactional(rollbackFor = Exception.class)
    public OrderPojo add(OrderPojo orderPojo,Map<Integer,String> barcodeMap) throws ApiException {
        for(OrderItemPojo item : orderPojo.getOrderItems()){
            String barcode = barcodeMap.get(item.getOrderItemId());
            item.setProductId(productApi.getByBarcode(barcode).getId());
        }
        boolean check = checkProductQuantity(orderPojo);
        if(!check){
            orderPojo.setStatus(OrderStatus.UNFULFILLABLE);
        }
        else{
            orderPojo.setStatus(OrderStatus.FULFILLABLE);
            updateInventory(orderPojo,true);
        }
        return orderApi.add(orderPojo);
    }

    @Transactional(rollbackFor = Exception.class)
    public OrderPojo retryOrder(String orderId) throws ApiException {
        OrderPojo orderPojo = orderApi.getByOrderId(orderId);
        if (Objects.isNull(orderPojo)) {
            throw new ApiException("Order not found with ID: " + orderId);
        }
        boolean check = checkProductQuantity(orderPojo);
        if(!check) {
            throw new ApiException("Order can not be fulfilled");
        } else {
            orderPojo.setStatus(OrderStatus.FULFILLABLE);
            orderPojo.setUpdatedAt(ZonedDateTime.now());
            updateInventory(orderPojo,true);
        }
        return orderApi.add(orderPojo);
    }   

    @Transactional(rollbackFor = Exception.class)
    public OrderPojo cancelOrder(String orderId) throws ApiException {
        OrderPojo order = orderApi.getByOrderId(orderId);
        if (Objects.isNull(order)) {
            throw new ApiException("Order not found with ID: " + orderId);
        }
        if (order.getStatus() == OrderStatus.FULFILLABLE) {
            updateInventory(order,false);
        }
        order.setUpdatedAt(ZonedDateTime.now());
        order.setStatus(OrderStatus.CANCELLED);
        return orderApi.add(order);
    }

    private void updateInventory(OrderPojo orderPojo,Boolean flag) throws ApiException {
        for (OrderItemPojo item : orderPojo.getOrderItems()) {
            InventoryPojo inventory = inventoryApi.getByProductId(item.getProductId());
            if(flag){
                inventoryApi.relativeUpdate(inventory, -item.getQuantity());
            }
            else{
                inventoryApi.relativeUpdate(inventory, item.getQuantity());
            }
        }
    }
    private boolean checkProductQuantity(OrderPojo orderPojo) throws ApiException {
        for (OrderItemPojo item : orderPojo.getOrderItems()) {
            InventoryPojo inventory = inventoryApi.getcheckByProductId(item.getProductId());
            if(Objects.isNull(inventory)){
                return false;
            }
            if (inventory.getQuantity() < item.getQuantity()) {
                return false;
            }
        }
        return true;
    }
}
