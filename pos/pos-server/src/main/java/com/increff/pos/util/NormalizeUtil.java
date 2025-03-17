package com.increff.pos.util;
import java.util.List;
import java.util.Objects;

import com.increff.pos.model.form.*;
import org.springframework.stereotype.Service;


import com.increff.pos.db.ProductPojo;

import java.util.Objects;

@Service
public class NormalizeUtil {
    public static String normalize(String data){
            return data.trim().toLowerCase();
        }

    public static ProductForm normalizeProductForm(ProductForm form){
        form.setBarcode(normalize(form.getBarcode()));
        form.setClientName(normalize(form.getClientName()));
        form.setProductName(normalize(form.getProductName()));
        form.setMrp(Math.round(form.getMrp() * 100.0) / 100.0);
        return form;
    }
    public static OrderForm normalizeOrderForm(OrderForm form){
        form.setCustomerName(normalize(form.getCustomerName()));
        form.setCustomerEmail(normalize(form.getCustomerEmail()));
        for(OrderItemForm item : form.getOrderItems()){
            item.setBarcode(normalize(item.getBarcode()));
            item.setQuantity(item.getQuantity());
            item.setSellingPrice(Math.round(item.getSellingPrice() * 100.0) / 100.0);
        }
        return form;
    }
    public static List<ProductForm> normalizeProductFormList(List<ProductForm> form){
        for(ProductForm productForm : form){
            productForm.setBarcode(normalize(productForm.getBarcode()));
            productForm.setClientName(normalize(productForm.getClientName()));
            productForm.setProductName(normalize(productForm.getProductName()));
            productForm.setMrp(Math.round(productForm.getMrp() * 100.0) / 100.0);
        }
        return form;
    }
    public static OrderSearchForm normalizeOrderSearchForm(OrderSearchForm form) {
        if (Objects.nonNull(form.getOrderId())) {
            form.setOrderId(form.getOrderId().trim());
        }
        
        if (Objects.nonNull(form.getStatus())) {
            form.setStatus(form.getStatus().trim().toUpperCase());
        }
        return form;
    }
    public static List<InventoryForm> normalizeInventoryFormList(List<InventoryForm> form){
        for(InventoryForm inventoryForm : form){
            inventoryForm.setBarcode(normalize(inventoryForm.getBarcode()));
        }
        return form;
    }

    public static void normalize(ClientForm form) {
            form.setName(form.getName().trim().toLowerCase());
    }
}
