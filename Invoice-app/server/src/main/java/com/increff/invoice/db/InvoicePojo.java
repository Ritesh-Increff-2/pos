package com.increff.invoice.db;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@CompoundIndex(name = "orderId_index", def = "{'orderId': 1}")
@Document(collection = "invoicedOrders")
public class InvoicePojo {
    @Id
    private String invoiceId;
    private String customerName;
    private String customerEmail;
    private Double orderTotal;
    private ZonedDateTime invoiceDate;
    private String invoiceFilePath;
    private List<InvoiceItemPojo> invoiceItems;

}
