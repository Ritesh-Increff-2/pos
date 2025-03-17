package com.increff.pos.dao;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.stereotype.Repository;

import com.increff.pos.db.OrderPojo;
import com.increff.pos.model.enums.OrderStatus;

@Repository
public class OrderDao extends AbstractDao<OrderPojo> {
    
    public OrderDao(MongoOperations mongoOperations) {
        super(
            new MongoRepositoryFactory(mongoOperations)
                .getEntityInformation(OrderPojo.class),
            mongoOperations
        );
    }

    public OrderPojo findByOrderId(String orderId) {
        return findByField("orderId", orderId);
    }
    

    public Void updateOrderStatusByOrderId(String orderId, String status) {
        OrderPojo orderPojo = findByOrderId(orderId);
        orderPojo.setStatus(OrderStatus.valueOf(status));
        mongoOperations.save(orderPojo);
        return null;
    }

    public Page<OrderPojo> searchOrders(String orderId, String status, 
    ZonedDateTime startDate, ZonedDateTime endDate, PageRequest pageRequest) {
    
    Criteria criteria = new Criteria();
    
    if (Objects.nonNull(orderId) && !orderId.trim().isEmpty()) {
        criteria.and("orderId").regex(orderId, "i");
    }
    
    if (Objects.nonNull(status) && !status.trim().isEmpty()) {
        criteria.and("status").is(status);
    }
    
    if (Objects.nonNull(startDate) && Objects.nonNull(endDate)) {
        criteria.and("orderTime").gte(startDate).lte(endDate);
    }
    
    // First create the criteria query without pagination to get total count
    Query countQuery = new Query(criteria);
    long total = mongoOperations.count(countQuery, OrderPojo.class);
    
    // Then create paginated query for fetching results
    Query paginatedQuery = new Query(criteria).with(pageRequest);
    List<OrderPojo> orders = mongoOperations.find(paginatedQuery, OrderPojo.class);
    
    return new PageImpl<>(orders, pageRequest, total);
}


}
