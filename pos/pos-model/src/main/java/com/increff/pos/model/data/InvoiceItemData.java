package com.increff.pos.model.data;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class InvoiceItemData {
    private String productName;
    private Integer quantity;
    private Double sellingPrice;
    private Double total;
}
