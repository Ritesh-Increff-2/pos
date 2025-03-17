package com.increff.pos.model.data;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ProductData {
    private String id;
    private String barcode;
    private String clientName;
    private String productName;
    private Double mrp;
    private String imageUrl;
} 