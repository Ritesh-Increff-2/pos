package com.increff.pos.api;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.pos.dao.DailySalesReportDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.db.DailySalesReportPojo;

@Service
public class DailySalesReportApiImpl implements DailySalesReportApi {
    @Autowired
    private DailySalesReportDao dailySalesReportDao;

    @Override
    public List<DailySalesReportPojo> getDailySalesReport(ZonedDateTime startDate, ZonedDateTime endDate) throws ApiException {
        return dailySalesReportDao.findBetweenDates(startDate, endDate);
    }
}
