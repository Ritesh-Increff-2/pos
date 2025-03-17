package com.increff.pos.model.data;

import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class DailySalesReportData {
    private String id;
    private ZonedDateTime date;
    private Integer totalInvoicedOrders;
    private Integer totalItems;
    private Double totalRevenue;
} 