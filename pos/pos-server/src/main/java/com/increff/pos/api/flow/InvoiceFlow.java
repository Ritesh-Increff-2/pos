package com.increff.pos.api.flow;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.pos.api.OrderApi;
import com.increff.pos.api.ProductApi;
import com.increff.pos.db.InvoiceItemPojo;
import com.increff.pos.db.InvoicePojo;
import com.increff.pos.db.OrderItemPojo;
import com.increff.pos.db.OrderPojo;
import com.increff.pos.db.ProductPojo;
import com.increff.pos.exception.ApiException;
@Service
public class InvoiceFlow {
    @Autowired
    private OrderApi orderApi;
    @Autowired
    private ProductApi productApi;

    public InvoicePojo generateInvoiceFlow(String orderId) throws ApiException {
        OrderPojo orderPojo = orderApi.getByOrderId(orderId);
        InvoicePojo invoicePojo = convertToPojo(orderPojo);
        orderApi.updateOrderStatusByOrderId(orderId, "INVOICED");
        return invoicePojo;
    }
    
    private InvoicePojo convertToPojo(OrderPojo orderPojo) throws ApiException{
        InvoicePojo invoicePojo = new InvoicePojo();
        invoicePojo.setInvoiceId(orderPojo.getOrderId());
        invoicePojo.setCustomerName(orderPojo.getCustomerName());
        invoicePojo.setCustomerEmail(orderPojo.getCustomerEmail());
        invoicePojo.setOrderTotal(orderPojo.getOrderTotal());
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        invoicePojo.setInvoiceDate(now);
        List<OrderItemPojo> orderItems = orderPojo.getOrderItems();
        List<InvoiceItemPojo> invoiceItems = new ArrayList<>();
        for(OrderItemPojo orderItem : orderItems){
            ProductPojo productPojo = productApi.getByProductID(orderItem.getProductId());
            if(Objects.isNull(productPojo)){
                throw new ApiException("Product not found with ID: " + orderItem.getProductId());
            }
            InvoiceItemPojo invoiceItemPojo = new InvoiceItemPojo();
            invoiceItemPojo.setProductName(productPojo.getProductName());
            invoiceItemPojo.setQuantity(orderItem.getQuantity());
            invoiceItemPojo.setSellingPrice(orderItem.getSellingPrice());
            invoiceItemPojo.setTotal(orderItem.getOrderItemTotal());
            invoiceItems.add(invoiceItemPojo);
        }
        invoicePojo.setInvoiceItems(invoiceItems);
        return invoicePojo;
    }
}
