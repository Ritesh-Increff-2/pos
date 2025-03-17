package com.increff.pos.db;

import java.time.ZonedDateTime;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@CompoundIndex(name = "idx_date", def = "{'date': 1}", unique = true)
@Document("daily_stats")
public class DailySalesReportPojo extends AbstractPojo {
    private ZonedDateTime date;
    private Integer totalInvoicedOrders;
    private Integer totalItems;
    private Double totalRevenue;
} 