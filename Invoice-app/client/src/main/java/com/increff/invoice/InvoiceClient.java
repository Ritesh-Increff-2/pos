package com.increff.invoice;

import org.springframework.web.client.RestTemplate;

import com.increff.invoice.data.InvoiceData;


public class InvoiceClient {

    private RestTemplate restTemplate;

    public InvoiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String generateInvoice(InvoiceData invoiceData) {
        String invoiceAppUrl = "http://localhost:8087/api/pdf/generate";
        return restTemplate.postForObject(invoiceAppUrl, invoiceData, String.class);
    }
    public String downloadInvoice(String invoiceId) {
        System.out.println(invoiceId);
        String invoiceAppUrl = "http://localhost:8087/api/pdf/download/" + invoiceId;
        return restTemplate.getForObject(invoiceAppUrl, String.class);
    }
}