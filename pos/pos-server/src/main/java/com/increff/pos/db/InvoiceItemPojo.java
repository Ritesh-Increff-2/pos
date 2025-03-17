package com.increff.pos.db;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceItemPojo extends AbstractPojo {
    private String productName;
    private Integer quantity;
    private Double sellingPrice;
    private Double total;
}
