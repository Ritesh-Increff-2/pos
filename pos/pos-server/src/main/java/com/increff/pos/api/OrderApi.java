package com.increff.pos.api;

import java.time.ZonedDateTime;

import org.springframework.data.domain.Page;

import com.increff.pos.db.OrderPojo;
import com.increff.pos.exception.ApiException;

public interface OrderApi {
    OrderPojo add(OrderPojo order) throws ApiException;
    OrderPojo getByOrderId(String OrderId) throws ApiException;
    Void updateOrderStatusByOrderId(String orderId,String status) throws ApiException;
    Page<OrderPojo> getAllWithPagination(Integer page,Integer size) throws ApiException;
    Page<OrderPojo> searchOrders(Integer page, Integer size, String orderId, String status, 
                       ZonedDateTime startDate, ZonedDateTime endDate) throws ApiException;
} 
