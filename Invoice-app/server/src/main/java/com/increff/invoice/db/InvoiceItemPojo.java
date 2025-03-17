package com.increff.invoice.db;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class InvoiceItemPojo {
    private String productName;
    private Integer quantity;
    private Double sellingPrice;
    private Double total;
}
