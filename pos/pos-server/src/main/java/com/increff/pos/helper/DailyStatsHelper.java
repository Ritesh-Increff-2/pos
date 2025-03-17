package com.increff.pos.helper;

import com.increff.pos.db.DailySalesReportPojo;
import com.increff.pos.model.data.DailySalesReportData;

public class DailyStatsHelper {
    
    public static DailySalesReportData convertToData(DailySalesReportPojo pojo) {
        DailySalesReportData data = new DailySalesReportData();
        data.setId(pojo.getId());
        data.setDate(pojo.getDate());
        data.setTotalInvoicedOrders(pojo.getTotalInvoicedOrders());
        data.setTotalItems(pojo.getTotalItems());
        data.setTotalRevenue(pojo.getTotalRevenue());
        return data;
    }
} 