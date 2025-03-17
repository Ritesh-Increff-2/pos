package com.increff.pos.api;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.increff.pos.db.DailySalesReportPojo;
import com.increff.pos.exception.ApiException;

@Service
public interface DailySalesReportApi {
    public List<DailySalesReportPojo> getDailySalesReport(ZonedDateTime startDate, ZonedDateTime endDate) throws ApiException;
}
