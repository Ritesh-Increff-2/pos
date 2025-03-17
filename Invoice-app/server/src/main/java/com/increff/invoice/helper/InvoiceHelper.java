package com.increff.invoice.helper;

import com.increff.invoice.data.InvoiceData;
import com.increff.invoice.data.InvoiceItemData;
import com.increff.invoice.db.InvoiceItemPojo;
import com.increff.invoice.db.InvoicePojo;

import java.util.ArrayList;
import java.util.List;
public class InvoiceHelper {
    public static InvoicePojo convertToPojo(InvoiceData data) {
        InvoicePojo pojo = new InvoicePojo();
        pojo.setInvoiceId(data.getInvoiceId());
        pojo.setInvoiceDate(data.getInvoiceDate());
        pojo.setOrderTotal(data.getOrderTotal());
        pojo.setCustomerName(data.getCustomerName());
        pojo.setCustomerEmail(data.getCustomerEmail());
        List<InvoiceItemPojo> invoiceItems = new ArrayList<>();
        for(InvoiceItemData invoiceItem : data.getInvoiceItems()){
            invoiceItems.add(convertToItemPojo(invoiceItem));
        }
        pojo.setInvoiceItems(invoiceItems);
        return pojo;
    }
    public static InvoiceItemPojo convertToItemPojo(InvoiceItemData data) {
        InvoiceItemPojo pojo = new InvoiceItemPojo();
        pojo.setProductName(data.getProductName());
        pojo.setQuantity(data.getQuantity());
        pojo.setSellingPrice(data.getSellingPrice());
        pojo.setTotal(data.getTotal());
        return pojo;
    }
}
