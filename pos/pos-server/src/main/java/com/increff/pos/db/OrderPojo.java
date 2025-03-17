package com.increff.pos.db;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.increff.pos.model.enums.OrderStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@CompoundIndex(name = "idx_orderId", def = "{'orderId': 1}", unique = true)
@CompoundIndex(name = "idx_orderTime", def = "{'orderTime': 1}")
@Document("orders")
public class OrderPojo extends AbstractPojo {
    private String customerName;
    private String customerEmail;
    private OrderStatus status;
    private ZonedDateTime orderTime;
    private String orderId;
    private Double orderTotal;
    private List<OrderItemPojo> orderItems;
}
