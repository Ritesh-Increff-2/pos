package com.increff.pos.Scheduler;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.increff.pos.dao.DailySalesReportDao;
import com.increff.pos.db.DailySalesReportPojo;
import com.increff.pos.model.enums.OrderStatus;

@Service
public class DailyStatsScheduler {

    @Autowired
    private MongoTemplate mongoTemplate;
   //TODO:: Move to DTO
    @Autowired
    private DailySalesReportDao dailyStatsDao;

    @Scheduled(fixedRate = 30*60*1000) // Runs every 30 minutes (1800000 milliseconds)
    public void updateDailyStats() {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime startOfDay = now.toLocalDate().atStartOfDay(ZoneOffset.UTC);
        ZonedDateTime endOfDay = startOfDay.plusDays(1);

        // Create aggregation pipeline
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(
                Criteria.where("status").is(OrderStatus.INVOICED.toString())
                    .and("orderTime").gte(startOfDay).lt(endOfDay)
            ),
            Aggregation.unwind("orderItems"),  // Unwind the orderItems array
            Aggregation.group()
                .count().as("totalInvoicedOrders")
                .sum("orderTotal").as("totalRevenue")
                .sum("orderItems.quantity").as("totalItems")
        );

        AggregationResults<DailySalesReportPojo> results = mongoTemplate.aggregate(
            aggregation, "orders", DailySalesReportPojo.class
        );

        if (!results.getMappedResults().isEmpty()) {
            DailySalesReportPojo stats = results.getMappedResults().get(0);
            stats.setDate(startOfDay);
            
            // Find existing entry for the day and update or create new
            DailySalesReportPojo existingStats = dailyStatsDao.findByDate(startOfDay);
            if (Objects.nonNull(existingStats)) {
                stats.setId(existingStats.getId());  
            }
            dailyStatsDao.save(stats);
        }
    }
} 