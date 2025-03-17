package com.increff.pos.model.data;

import java.time.ZonedDateTime;
import java.util.List;

import com.increff.pos.model.enums.OrderStatus;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class OrderData {
    private String orderId;
    private OrderStatus status;
    private String customerName;
    private String customerEmail;
    private String id;
    private ZonedDateTime orderTime;
    private Double orderTotal;
    private List<OrderItemData> orderItems;
}