package com.increff.pos.dto;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.pos.api.DailySalesReportApi;
import com.increff.pos.dao.DailySalesReportDao;
import com.increff.pos.db.DailySalesReportPojo;
import com.increff.pos.exception.ApiException;
import com.increff.pos.helper.DailyStatsHelper;
import com.increff.pos.model.data.DailySalesReportData;
import com.increff.pos.model.form.DateFilterForm;
import com.increff.pos.util.ValidationUtil;

@Service
public class DailySalesReportDto {

    @Autowired
    private DailySalesReportApi dailySalesReportApi;

    public List<DailySalesReportData> getDailyStats(DateFilterForm form) throws ApiException {
        ValidationUtil.validateDataForm(form);
        List<DailySalesReportPojo> pojos = dailySalesReportApi.getDailySalesReport(form.getStartDate(), form.getEndDate());
        return pojos.stream()
                   .map(DailyStatsHelper::convertToData)
                   .collect(Collectors.toList());
    }
} 