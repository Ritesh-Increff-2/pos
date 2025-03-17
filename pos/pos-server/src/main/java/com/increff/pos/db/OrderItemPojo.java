package com.increff.pos.db;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemPojo {
    private Integer orderItemId;
    private String productId;
    private Integer quantity;
    private Double sellingPrice;
    private Double orderItemTotal;
}
