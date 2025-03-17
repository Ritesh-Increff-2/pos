package com.increff.pos.model.data;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class FullProductData {
    private String barcode;
    private String clientName;
    private String productName;
    private Double mrp;
    private String imageUrl;
    private Integer inventoryQuantity;
}
