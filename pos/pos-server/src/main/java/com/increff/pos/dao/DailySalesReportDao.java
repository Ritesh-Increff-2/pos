package com.increff.pos.dao;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.stereotype.Repository;

import com.increff.pos.db.DailySalesReportPojo;

@Repository
public class DailySalesReportDao extends AbstractDao<DailySalesReportPojo> {
    public DailySalesReportDao(MongoOperations mongoOperations) {
        super(
            new MongoRepositoryFactory(mongoOperations)
                .getEntityInformation(DailySalesReportPojo.class),
            mongoOperations
        );
    }

    public DailySalesReportPojo findByDate(ZonedDateTime date) {
        return findByField("date", date);
    }

    public List<DailySalesReportPojo> findBetweenDates(ZonedDateTime startDate, ZonedDateTime endDate) {
        return findByDateRange("date", startDate, endDate, PageRequest.of(0, Integer.MAX_VALUE)).getContent();
    }
} 