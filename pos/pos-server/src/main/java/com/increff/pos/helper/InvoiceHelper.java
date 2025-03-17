package com.increff.pos.helper;

import java.util.ArrayList;
import java.util.List;

import com.increff.invoice.data.InvoiceData;
import com.increff.invoice.data.InvoiceItemData;
import com.increff.pos.db.InvoiceItemPojo;
import com.increff.pos.db.InvoicePojo;

public class InvoiceHelper {

    public static InvoiceData convertToData(InvoicePojo pojo) {
        InvoiceData data = new InvoiceData();
        data.setInvoiceId(pojo.getInvoiceId());
        data.setInvoiceDate(pojo.getInvoiceDate());
        data.setOrderTotal(pojo.getOrderTotal());
        data.setCustomerName(pojo.getCustomerName());
        data.setCustomerEmail(pojo.getCustomerEmail());
        List<InvoiceItemPojo> invoiceItems = pojo.getInvoiceItems();
        List<InvoiceItemData> invoiceItemData = new ArrayList<>();
        for(InvoiceItemPojo invoiceItem : invoiceItems){
            invoiceItemData.add(convertToItemData(invoiceItem));
        }
        data.setInvoiceItems(invoiceItemData);
        

        return data;
    }
    public static InvoiceItemData convertToItemData(InvoiceItemPojo pojo) {
        InvoiceItemData data = new InvoiceItemData();
        data.setProductName(pojo.getProductName());
        data.setQuantity(pojo.getQuantity());
        data.setSellingPrice(pojo.getSellingPrice());
        data.setTotal(pojo.getTotal());
        return data;
    }
    
}
