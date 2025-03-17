package com.increff.pos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.increff.pos.dto.SalesReportDto;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.SalesReportData;
import com.increff.pos.model.form.DateFilterForm;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
@CrossOrigin(origins = "*")
@Tag(name = "Sales Report", description = "APIs for sales report")
@RestController
@RequestMapping("/api/sales-report")
public class SalesReportController {
    @Autowired
    private SalesReportDto salesReportDto;

    @Operation(summary = "Get sales report")
    @RequestMapping(path = "/get", method = RequestMethod.POST)
    public List<SalesReportData> getSalesReport(@RequestBody DateFilterForm dateFilterForm,@RequestParam(required = false) String clientName) throws ApiException {
        return salesReportDto.getSalesReport(dateFilterForm, clientName);
    }
}
