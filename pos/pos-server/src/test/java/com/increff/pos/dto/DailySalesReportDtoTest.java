package com.increff.pos.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.DailySalesReportData;
import com.increff.pos.model.form.DateFilterForm;
import com.increff.pos.test.AbstractUnitTest;

public class DailySalesReportDtoTest extends AbstractUnitTest {

    @Autowired
    private DailySalesReportDto dailySalesReportDto;

    @Test
    void testGetDailyStats() throws ApiException {
        // Create date filter form with valid dates
        DateFilterForm form = new DateFilterForm();
        form.setStartDate(ZonedDateTime.now().minusDays(7));
        form.setEndDate(ZonedDateTime.now());

        // Get daily stats
        List<DailySalesReportData> statsList = dailySalesReportDto.getDailyStats(form);
        
        // Verify results
        assertNotNull(statsList);
    }

    @Test
    void testGetDailyStatsWithInvalidDates() {
        // Create date filter form with invalid dates (start date after end date)
        DateFilterForm form = new DateFilterForm();
        form.setStartDate(ZonedDateTime.now().plusDays(1));
        form.setEndDate(ZonedDateTime.now().minusDays(1));

        // Verify that ApiException is thrown
        assertThrows(ApiException.class, () -> dailySalesReportDto.getDailyStats(form));
    }

    @Test
    void testGetDailyStatsWithNullDates() {
        // Create date filter form with null dates
        DateFilterForm form = new DateFilterForm();
        form.setStartDate(null);
        form.setEndDate(null);

        // Verify that ApiException is thrown
        assertThrows(ApiException.class, () -> dailySalesReportDto.getDailyStats(form));
    }
}