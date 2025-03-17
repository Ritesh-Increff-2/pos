package com.increff.pos.model.form;

import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class DateFilterForm {
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
}