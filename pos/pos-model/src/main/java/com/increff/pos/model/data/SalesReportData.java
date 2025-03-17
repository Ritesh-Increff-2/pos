package com.increff.pos.model.data;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class SalesReportData {
    private String clientName;
    private String productName;
    private double quantity;
    private double sellingPrice;
    private double revenue;
    
}
