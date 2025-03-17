package com.increff.pos.api;

import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.increff.pos.dao.OrderDao;
import com.increff.pos.db.OrderPojo;
import com.increff.pos.exception.ApiException;

@Service
public class OrderApiImpl implements OrderApi {
    @Autowired
    private OrderDao orderDao;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderPojo add(OrderPojo order) throws ApiException {
            return orderDao.save(order);
    }

    @Override
    public OrderPojo getByOrderId(String orderId) throws ApiException {
        return orderDao.findByOrderId(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void updateOrderStatusByOrderId(String orderId,String status) throws ApiException {
        orderDao.updateOrderStatusByOrderId(orderId,status);
        return null;
    }

    @Override
    public Page<OrderPojo> getAllWithPagination(Integer page,Integer size) throws ApiException {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return orderDao.findAll(pageRequest);
    }

    @Override
    public Page<OrderPojo> searchOrders(Integer page, Integer size, String orderId, 
        String status, ZonedDateTime startDate, ZonedDateTime endDate) throws ApiException {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return orderDao.searchOrders(orderId, status, startDate, endDate, pageRequest);
    }

}
