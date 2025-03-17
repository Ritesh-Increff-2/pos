package com.increff.pos.dto;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.invoice.InvoiceClient;
import com.increff.invoice.data.InvoiceData;
import com.increff.pos.api.flow.InvoiceFlow;
import com.increff.pos.db.InvoicePojo;
import com.increff.pos.exception.ApiException;
import com.increff.pos.helper.InvoiceHelper;

@Service
public class InvoiceDto {
    @Autowired
    private InvoiceFlow invoiceFlow;
    @Autowired
    private InvoiceClient invoiceClient;

    public String generateInvoice(String orderId) throws ApiException {
        InvoicePojo invoicePojo = invoiceFlow.generateInvoiceFlow(orderId);
        InvoiceData invoiceData = InvoiceHelper.convertToData(invoicePojo);
        return invoiceClient.generateInvoice(invoiceData);
    }
    public String getPdfBase64(String orderId) throws ApiException {
        String pdfPath = invoiceClient.downloadInvoice(orderId);
        try {
            File file = new File(pdfPath);
            byte[] bytes = Files.readAllBytes(file.toPath());
            return Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            throw new ApiException("Error reading file: " + e.getMessage());
        }
    }
}
