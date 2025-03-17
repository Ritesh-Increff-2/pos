package com.increff.pos.util;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.increff.pos.exception.ApiException;
import com.increff.pos.model.form.ClientForm;
import com.increff.pos.model.form.ClientUpdateForm;
import com.increff.pos.model.form.DataPageForm;
import com.increff.pos.model.form.DateFilterForm;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.model.form.OrderForm;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.model.form.PageForm;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.model.form.SearchForm;
import com.increff.pos.model.form.OrderSearchForm;
import com.increff.pos.model.enums.OrderStatus;

public class ValidationUtil {

    public static void validateClientForm(ClientForm form) throws ApiException {
        validateClientName(form.getName());
    }

    public static void validateClientName(String name) throws ApiException {
        if (Objects.isNull(name.trim()) || name.trim().isEmpty()) {
            throw new ApiException("Client name cannot be empty");
        }
        if (name.length() > 50) {
            throw new ApiException("Client name must be 50 characters or less.");
        }
    }

    public static void validateBarcode(String barcode) throws ApiException {
        if (!StringUtils.hasText(barcode)) {
            throw new ApiException("Barcode cannot be empty");
        }
        if (barcode.length() > 15) {
            throw new ApiException("Barcode must be 15 characters or less.");
        }
    }
    
    public static void validateEmail(String email) throws ApiException {
        if (!StringUtils.hasText(email)) {
            throw new ApiException("Email cannot be empty");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ApiException("Invalid email format");
        }
    }

    public static void validatePageForm(PageForm form) throws ApiException {
        if (form.getPage() < 0) {
            throw new ApiException("Page number cannot be negative");
        }

        if (form.getSize() > 100) {
            throw new ApiException("Page size cannot be greater than 100");
        }
        if (form.getSize() <= 0) {
            throw new ApiException("Page size must be positive");
        }
    }

    public static void validateProductForm(ProductForm form) throws ApiException {
        if (!StringUtils.hasText(form.getBarcode())) {
            throw new ApiException("Barcode cannot be empty");
        }
        if (form.getBarcode().length() > 15) {
            throw new ApiException("Barcode must be 15 characters or less.");
        }
        if (!StringUtils.hasText(form.getClientName())) {
            throw new ApiException("Client name cannot be empty");
        }
        if (form.getClientName().length() > 50) {
            throw new ApiException("Client name must be 50 characters or less.");
        }
        if (!StringUtils.hasText(form.getProductName())) {
            throw new ApiException("Product name cannot be empty");
        }
        
        if (form.getProductName().length() > 50) {
            throw new ApiException("Product name must be 50 characters or less.");
        }
        if (Objects.isNull(form.getMrp()) || form.getMrp() <= 0) {
            throw new ApiException("MRP must be greater than 0");
        }
        if (Objects.isNull(form.getImageUrl()) || form.getImageUrl().trim().isEmpty()) {
            throw new ApiException("Image URL cannot be empty");
        }
    }

    public static void validateClientUpdateForm(ClientUpdateForm form) throws ApiException {
        validateClientName(form.getNewClientName());
        validateClientName(form.getOldClientName());
    }

    public static void validateInventoryForm(InventoryForm form) throws ApiException {
        validateBarcode(form.getBarcode());
        if (Objects.isNull(form.getQuantity()) || form.getQuantity() < 0 ) {
            throw new ApiException("Quantity must be non-negative");
        }
        
    }

    public static void validateOrderForm(OrderForm form) throws ApiException {
        if (Objects.isNull(form) || Objects.isNull(form.getOrderItems()) || form.getOrderItems().isEmpty()) {
            throw new ApiException("Order items cannot be empty");
        }
        for (OrderItemForm item : form.getOrderItems()) {
            if (Objects.isNull(item)) {
                throw new ApiException("Order item cannot be null");
            }
            if (!StringUtils.hasText(item.getBarcode())) {
                throw new ApiException("Barcode cannot be empty");
            }
            if(item.getBarcode().length() > 15) {
                throw new ApiException("Barcode must be 15 characters or less.");
            }
            if (Objects.isNull(item.getQuantity()) || item.getQuantity() < 1) {
                throw new ApiException("Invalid quantity for barcode: " + item.getBarcode());
            }
            if(item.getQuantity() > 1000){
                throw new ApiException("Quantity cannot be greater than 1000");
            }
            if (Objects.isNull(item.getSellingPrice()) || item.getSellingPrice() < 0) {
                throw new ApiException("Invalid selling price for barcode: " + item.getBarcode());
            }
            if(item.getSellingPrice() > 1000000){
                throw new ApiException("Selling price cannot be greater than 1000000");
            }


            validateEmail(form.getCustomerEmail());
        }
        if (!StringUtils.hasText(form.getCustomerName())) {
            throw new ApiException("Customer name cannot be empty");
        }
        if(form.getCustomerName().length() > 50) {
            throw new ApiException("Customer name must be 50 characters or less.");
        }
    }

    public static void validateTsvFile(MultipartFile file) throws ApiException {
        if (file.isEmpty()) {
            throw new ApiException("File is empty");
        }

    }

    public static void validateProductFormList(List<ProductForm> productForms) throws ApiException {
        if (productForms.size() > 100) {
            throw new ApiException("File contains more than 100 entries");
        }
        Set<String> barcodes = new HashSet<>();
        for (ProductForm form : productForms) {
            if (!barcodes.add(form.getBarcode())) {
                throw new ApiException("Duplicate barcode found in file: " + form.getBarcode());
            }
        }
        for (ProductForm form : productForms) {
            validateProductForm(form);
        }
       

    }

    public static void validateInventoryFormList(List<InventoryForm> inventoryForms) throws ApiException {
        if (inventoryForms.size() > 100) {
            throw new ApiException("File contains more than 100 entries");
        }
        for (InventoryForm form : inventoryForms) {
            validateInventoryForm(form);
        }
        Set<String> barcodes = new HashSet<>();
        for (InventoryForm form : inventoryForms) {
            if (!barcodes.add(form.getBarcode())) {
                throw new ApiException("Duplicate barcode found in file: " + form.getBarcode());
            }
        }
    }

    public static void validateDataForm(DateFilterForm form) throws ApiException {
        if (Objects.isNull(form)) {
            throw new ApiException("Form cannot be null");
        }
        if (Objects.isNull(form.getStartDate())) {
            throw new ApiException("Start date cannot be null");
        }
        if (Objects.isNull(form.getEndDate())) {
            throw new ApiException("End date cannot be null");
        }
        if (form.getEndDate().isBefore(form.getStartDate())) {
            throw new ApiException("End date cannot be before start date");
        }
    }

    


    public static void validateOrderSearchForm(OrderSearchForm form) throws ApiException {
        if (Objects.isNull(form)) {
            throw new ApiException("Form cannot be null");
        }

        if (form.getPage() < 0) {
            throw new ApiException("Page number cannot be negative");
        }
        if (form.getSize() <= 0) {
            throw new ApiException("Page size must be positive");
        }
        if (form.getStartDate() != null && form.getEndDate() != null) {
            if (form.getStartDate().isAfter(form.getEndDate())) {
                throw new ApiException("Start date cannot be after end date");
            }
        }
        if (form.getStatus() != null && !form.getStatus().trim().isEmpty()) {
            try {
                OrderStatus.valueOf(form.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ApiException("Invalid order status");
            }
        }
    }

    public static void validateQuantity(String quantity) throws ApiException {
        if (Objects.isNull(quantity) || quantity.trim().isEmpty()) {
            throw new ApiException("Quantity cannot be empty");
        }
        try {
            Integer.parseInt(quantity);
        } catch (NumberFormatException e) {
            throw new ApiException("Quantity must be a valid integer");
        }
    }

} 