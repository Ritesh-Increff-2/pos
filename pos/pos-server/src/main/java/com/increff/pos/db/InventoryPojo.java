package com.increff.pos.db;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@CompoundIndex(name = "idx_productId", def = "{'productId': 1}", unique = true)
@Document(collection = "inventory")
public class InventoryPojo extends AbstractPojo {
    private String productId;     
    private Integer quantity;
} 