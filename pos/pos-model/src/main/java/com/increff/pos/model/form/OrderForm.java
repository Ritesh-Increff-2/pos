package com.increff.pos.model.form;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class OrderForm {
    private String customerName;
    private String customerEmail;
    private List<OrderItemForm> orderItems;
}
