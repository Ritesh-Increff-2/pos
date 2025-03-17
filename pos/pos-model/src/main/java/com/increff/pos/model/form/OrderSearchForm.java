package com.increff.pos.model.form;

import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderSearchForm {
    private String orderId;
    private String status;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private int page;
    private int size;
} 