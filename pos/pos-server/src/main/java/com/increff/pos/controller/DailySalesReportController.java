package com.increff.pos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.increff.pos.dto.DailySalesReportDto;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.DailySalesReportData;
import com.increff.pos.model.form.DateFilterForm;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@Tag(name = "Daily Statistics", description = "APIs for daily order statistics")
@RestController
@RequestMapping("/api/daily-stats")
public class DailySalesReportController {

    @Autowired
    private DailySalesReportDto dailySalesReportDto;

    @Operation(summary = "Get daily statistics between dates")
    @RequestMapping(path = "", method = RequestMethod.POST)
    public List<DailySalesReportData> getDailyStats(@RequestBody DateFilterForm dateFilterForm) throws ApiException {
        return dailySalesReportDto.getDailyStats(dateFilterForm);
    }
} 