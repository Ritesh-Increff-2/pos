package com.increff.invoice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.increff.invoice.Dto.InvoiceDto;
import com.increff.invoice.data.InvoiceData;
import com.increff.invoice.exception.ApiException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/pdf")
public class InvoiceController {
    @Autowired
    private InvoiceDto invoiceDto;

    @RequestMapping(value = "/generate", method = RequestMethod.POST)
    public String generatePdf(@RequestBody InvoiceData invoiceData) throws Exception {
        return invoiceDto.generateInvoice(invoiceData);
    }
    @RequestMapping(value = "/download/{invoiceId}", method = RequestMethod.GET)
    public String downloadPdf(@PathVariable String invoiceId) throws ApiException {
        return invoiceDto.downloadInvoice(invoiceId);
    }   
    
}
