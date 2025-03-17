package com.increff.pos.model.data;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class InventoryData {
    private String id;
    private String barcode;
    private String productName;
    private Integer quantity;
} 