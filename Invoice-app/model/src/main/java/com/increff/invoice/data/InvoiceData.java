package com.increff.invoice.data;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;
@Getter
@Setter
public class InvoiceData {
    private String invoiceId;
    private String customerName;
    private String customerEmail;
    private ZonedDateTime invoiceDate;
    private Double orderTotal;
    private List<InvoiceItemData> invoiceItems;
}
