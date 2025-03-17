package com.increff.pos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.increff.pos.dto.InvoiceDto;
import com.increff.pos.exception.ApiException;

import io.swagger.v3.oas.annotations.Operation;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {
    @Autowired
    private InvoiceDto invoiceDto;


    @Operation(summary = "Generate Invoice")
    @RequestMapping(path = "/generate/{orderId}", method = RequestMethod.POST)
    public String generateInvoice(@PathVariable String orderId) throws ApiException {
     return invoiceDto.generateInvoice(orderId);
    }

    @RequestMapping(path = "/download/{orderId}", method = RequestMethod.GET)
    public String downloadInvoice(@PathVariable String orderId) throws ApiException {
        return invoiceDto.getPdfBase64(orderId);
    }
}
