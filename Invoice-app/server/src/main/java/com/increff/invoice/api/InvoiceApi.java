package com.increff.invoice.api;


import org.springframework.stereotype.Service;

import com.increff.invoice.data.InvoiceData;
import com.increff.invoice.exception.ApiException;
@Service
public interface InvoiceApi {
    String generateInvoice(InvoiceData invoiceData) throws Exception;
    String downloadInvoice(String orderId) throws ApiException;
}