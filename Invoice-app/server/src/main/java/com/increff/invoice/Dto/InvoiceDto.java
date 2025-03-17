package com.increff.invoice.Dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.invoice.api.InvoiceApi;
import com.increff.invoice.data.InvoiceData;
import com.increff.invoice.exception.ApiException;

@Service
public class InvoiceDto {
    @Autowired
    private InvoiceApi invoiceApi;

    public String generateInvoice(InvoiceData invoiceData) throws Exception {
        return invoiceApi.generateInvoice(invoiceData);
    }
    public String downloadInvoice(String invoiceId) throws ApiException {
        return invoiceApi.downloadInvoice(invoiceId);
    }
    
}
