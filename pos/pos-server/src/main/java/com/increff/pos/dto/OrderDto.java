package com.increff.pos.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.increff.pos.api.OrderApi;
import com.increff.pos.api.flow.OrderFlow;
import com.increff.pos.db.OrderPojo;
import com.increff.pos.exception.ApiException;
import com.increff.pos.helper.OrderHelper;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.form.DataPageForm;
import java.util.Map;
import java.util.HashMap;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.model.form.OrderForm;
import com.increff.pos.model.form.PageForm;
import com.increff.pos.model.form.OrderSearchForm;
import com.increff.pos.util.NormalizeUtil;

import com.increff.pos.util.ValidationUtil;
import org.springframework.stereotype.Component;

@Service
@Component
public class OrderDto {
    @Autowired
    private OrderApi orderApi;  

    @Autowired
    private OrderFlow orderFlow;
    
    public OrderData addOrder(OrderForm form) throws ApiException {
        ValidationUtil.validateOrderForm(form);
        NormalizeUtil.normalizeOrderForm(form);
        OrderPojo orderPojo = OrderHelper.convertToEntity(form);
        Map<Integer,String> barcodeMap = new HashMap<>();
        int i = 0;
        for(OrderItemForm item : form.getOrderItems()){
            barcodeMap.put(orderPojo.getOrderItems().get(i).getOrderItemId(),item.getBarcode());
            i++;
        }
        OrderPojo addedOrderPojo = orderFlow.add(orderPojo,barcodeMap);
        return OrderHelper.convertToData(addedOrderPojo);
    }

    public OrderData cancelOrder(String orderId) throws ApiException {
        OrderPojo pojo = orderFlow.cancelOrder(orderId);
        return OrderHelper.convertToData(pojo);
    }

    public OrderData retryOrder(String orderId) throws ApiException {
        OrderPojo pojo = orderFlow.retryOrder(orderId);
        return OrderHelper.convertToData(pojo);
    }

    public Page<OrderData> getAllOrdersWithPagination(PageForm form) throws ApiException {
        ValidationUtil.validatePageForm(form);
        Page<OrderPojo> pojos = orderApi.getAllWithPagination(form.getPage(),form.getSize());
        return pojos.map(OrderHelper::convertToData);
    }

    public Page<OrderData> searchOrders(OrderSearchForm form) throws ApiException {
        ValidationUtil.validateOrderSearchForm(form);
        NormalizeUtil.normalizeOrderSearchForm(form);
        String orderId = StringUtils.hasText(form.getOrderId()) ? form.getOrderId() : null;
        String status = StringUtils.hasText(form.getStatus()) ? form.getStatus() : null;
        Page<OrderPojo> orderPage = orderApi.searchOrders(
            form.getPage(),
            form.getSize(),
            orderId,
            status,
            form.getStartDate(),
            form.getEndDate()
        );
        return orderPage.map(OrderHelper::convertToData);
    }

}
