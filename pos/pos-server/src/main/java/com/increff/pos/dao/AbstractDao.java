package com.increff.pos.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

public abstract class AbstractDao<T> extends SimpleMongoRepository<T, String> {
    protected final MongoOperations mongoOperations;
    private final Class<T> entityClass;
    
    public AbstractDao(MongoEntityInformation<T, String> entityInformation, MongoOperations mongoOperations) {
        super(entityInformation, mongoOperations);
        this.mongoOperations = mongoOperations;
        this.entityClass = entityInformation.getJavaType();
    }

    // Generic method for finding by any field with exact match
    protected T findByField(String fieldName, Object value) {
        Query query = Query.query(Criteria.where(fieldName).is(value));
        return mongoOperations.findOne(query, entityClass);
    }

    // Generic method for searching with regex pattern and pagination
    protected Page<T> searchByFieldPattern(String fieldName, String searchPattern, PageRequest pageRequest) {
        Query query = Query.query(Criteria.where(fieldName).regex(searchPattern, "i"));
        long totalElements = mongoOperations.count(query, entityClass);
        query.with(pageRequest);
        List<T> results = mongoOperations.find(query, entityClass);
        return new PageImpl<>(results, pageRequest, totalElements);
    }

    // Generic method for date range queries
    protected Page<T> findByDateRange(String dateField, Object startDate, Object endDate, PageRequest pageRequest) {
        Query query = Query.query(Criteria.where(dateField).gte(startDate).lte(endDate));
        long totalElements = mongoOperations.count(query, entityClass);
        query.with(pageRequest);
        List<T> results = mongoOperations.find(query, entityClass);
        return new PageImpl<>(results, pageRequest, totalElements);
    }
}