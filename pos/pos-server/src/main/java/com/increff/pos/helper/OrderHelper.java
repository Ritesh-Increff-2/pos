package com.increff.pos.helper;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import com.increff.pos.db.OrderItemPojo;
import com.increff.pos.db.OrderPojo;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.form.OrderForm;
import com.increff.pos.model.form.OrderItemForm;


public class OrderHelper {
    private static int itemCounter ;
    private static double orderTotal;

    public static OrderPojo convertToEntity(OrderForm form) {
        itemCounter = 1;
        orderTotal = 0.0;
        OrderPojo pojo = new OrderPojo();
        pojo.setCreatedAt(ZonedDateTime.now());
        pojo.setUpdatedAt(ZonedDateTime.now());
        List<OrderItemPojo> orderItems = form.getOrderItems().stream()
            .map(OrderHelper::convertToItemEntity)
            .collect(Collectors.toList());
        pojo.setCustomerName(form.getCustomerName());
        pojo.setCustomerEmail(form.getCustomerEmail());
        pojo.setOrderItems(orderItems);
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        pojo.setOrderTime(now);
        pojo.setOrderTotal(orderTotal);
        String tempInvoiceNo = String.format("ORD%s%02d%02d%02d",
            now.format(DateTimeFormatter.BASIC_ISO_DATE),
            now.getHour(),
            now.getMinute(),
            now.getSecond());
        pojo.setOrderId(tempInvoiceNo);
        return pojo;
    }
    public static OrderData convertToData(OrderPojo pojo) {
        OrderData data = new OrderData();
        data.setId(pojo.getId());
        data.setOrderTime(pojo.getOrderTime());
        List<OrderItemData> items = pojo.getOrderItems().stream()
                .map(OrderHelper::convertToItemData)
                .collect(Collectors.toList());
        data.setCustomerName(pojo.getCustomerName());
        data.setCustomerEmail(pojo.getCustomerEmail());
        data.setOrderItems(items);
        data.setStatus(pojo.getStatus());
        data.setOrderId(pojo.getOrderId());
        data.setOrderTotal(pojo.getOrderTotal());
       
        return data;
    }
    private static OrderItemPojo convertToItemEntity(OrderItemForm form) {
        OrderItemPojo pojo = new OrderItemPojo();
        pojo.setOrderItemId(itemCounter++);
        pojo.setQuantity(form.getQuantity());
        pojo.setSellingPrice(form.getSellingPrice());
        pojo.setOrderItemTotal(form.getQuantity() * form.getSellingPrice());
        orderTotal += pojo.getOrderItemTotal();
        return pojo;
    }
    private static OrderItemData convertToItemData(OrderItemPojo pojo) {
        OrderItemData data = new OrderItemData();
        data.setOrderItemId(pojo.getOrderItemId());
        data.setProductId(pojo.getProductId());
        data.setQuantity(pojo.getQuantity());
        data.setSellingPrice(pojo.getSellingPrice());
        data.setOrderItemTotal(pojo.getOrderItemTotal());
        return data;
    }
    
}
