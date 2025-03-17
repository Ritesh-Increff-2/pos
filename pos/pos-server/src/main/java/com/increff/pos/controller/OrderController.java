package com.increff.pos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.increff.pos.dto.OrderDto;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.form.PageForm;
import com.increff.pos.model.form.OrderSearchForm;
import com.increff.pos.model.form.OrderForm;

import io.swagger.v3.oas.annotations.Operation;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderDto orderDto;

    @Operation(summary = "Add a new order")
    @RequestMapping(path = "", method = RequestMethod.POST)
    public OrderData addOrder(@RequestBody OrderForm orderform) throws ApiException {
        return orderDto.addOrder(orderform);
    }

    @Operation(summary = "Cancel by orderId")
    @RequestMapping(path="/cancel/{orderId}",method = RequestMethod.POST)
    public OrderData cancelOrder(@PathVariable String orderId) throws ApiException {
        return orderDto.cancelOrder(orderId);
    }

    @Operation(summary ="Retry Order")
    @RequestMapping(path="/{orderId}",method = RequestMethod.PUT)
    public OrderData retryOrder(@PathVariable String orderId) throws ApiException {
        return orderDto.retryOrder(orderId);
    }

    @Operation(summary = "Get all orders with pagination")
    @RequestMapping(path = "/get",method = RequestMethod.POST)
    public Page<OrderData> getAllOrdersWithPagination(@RequestBody PageForm form) throws ApiException {
        return orderDto.getAllOrdersWithPagination(form);
    }

    @Operation(summary = "Get all orders with pagination and optional search filters")
    @RequestMapping(path = "/search", method = RequestMethod.POST)
    public Page<OrderData> searchOrders(@RequestBody OrderSearchForm form) throws ApiException {
        return orderDto.searchOrders(form);
    }


}