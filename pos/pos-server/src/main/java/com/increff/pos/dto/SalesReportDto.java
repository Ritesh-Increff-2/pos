package com.increff.pos.dto;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.increff.pos.api.flow.SalesReportFlow;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.SalesReportData;
import com.increff.pos.model.form.DateFilterForm;
import com.increff.pos.util.ValidationUtil;
@Service
public class SalesReportDto {
    @Autowired
    private SalesReportFlow salesReportFlow;

    public List<SalesReportData> getSalesReport(DateFilterForm salesReportForm, String clientName) throws ApiException {
        ValidationUtil.validateDataForm(salesReportForm);
        return salesReportFlow.getSalesReport(
            salesReportForm.getStartDate(), 
            salesReportForm.getEndDate(),
            clientName
        );

    }
}
