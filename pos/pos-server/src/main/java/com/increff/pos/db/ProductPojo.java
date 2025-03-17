package com.increff.pos.db;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@CompoundIndex(name = "idx_barcode", def = "{'barcode': 1}", unique = true)
@Document(collection = "products")
public class ProductPojo extends AbstractPojo {
    private String barcode;
    private String clientName;
    private String productName;
    private Double mrp;
    private String imageUrl;
} 