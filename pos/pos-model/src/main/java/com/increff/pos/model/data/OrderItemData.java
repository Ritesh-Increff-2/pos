package com.increff.pos.model.data;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class OrderItemData {
    private Integer orderItemId;
    private String productId;
    private Integer quantity;
    private Double sellingPrice;
    private Double orderItemTotal;
    // private Double 
}