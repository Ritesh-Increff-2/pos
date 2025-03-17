package com.increff.pos.db;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.mongodb.core.index.CompoundIndex;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoicePojo extends AbstractPojo {
    private String invoiceId;
    private String customerName;
    private String customerEmail;
    private Double orderTotal;
    private ZonedDateTime invoiceDate;
    private List<InvoiceItemPojo> invoiceItems;
}
