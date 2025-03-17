package com.increff.pos.dto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.form.ClientForm;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.model.form.OrderForm;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.test.AbstractUnitTest;

public class InvoiceDtoTest extends AbstractUnitTest {

    @Autowired
    private InvoiceDto invoiceDto;

    @Autowired
    private OrderDto orderDto;

    @Autowired
    private ProductDto productDto;

    @Autowired
    private InventoryDto inventoryDto;

    @Autowired
    private ClientDto clientDto;

    private String testBarcode;
    private String testClient;
    private String orderId;

    @BeforeEach
    void setUp() throws ApiException {
        // Create test client
        testClient = "test client";
        ClientForm clientForm = new ClientForm();
        clientForm.setName(testClient);
        clientDto.addClient(clientForm);

        // Create test product
        testBarcode = "TEST123";
        ProductForm productForm = new ProductForm();
        productForm.setBarcode(testBarcode);
        productForm.setClientName(testClient);
        productForm.setProductName("test product");
        productForm.setMrp(100.0);
        productForm.setImageUrl("http://example.com/test.jpg");
        productDto.addProduct(productForm);

        // Add inventory
        InventoryForm inventoryForm = new InventoryForm();
        inventoryForm.setBarcode(testBarcode);
        inventoryForm.setQuantity(100);
        inventoryDto.addInventory(inventoryForm);

        // Create test order
        OrderForm orderForm = new OrderForm();
        orderForm.setCustomerName("Test Customer");
        orderForm.setCustomerEmail("test@example.com");
        List<OrderItemForm> items = new ArrayList<>();
        OrderItemForm item = new OrderItemForm(testBarcode, 5, 90.0);
        items.add(item);
        orderForm.setOrderItems(items);

        // Save order and get orderId
        OrderData orderData = orderDto.addOrder(orderForm);
        orderId = orderData.getOrderId();
    }

    @Test
    void testGenerateInvoice() throws ApiException {
        // Test invoice generation
        String invoicePath = invoiceDto.generateInvoice(orderId);
        assertNotNull(invoicePath);
        assertTrue(invoicePath.length() > 0);
    }

    @Test
    void testDownloadInvoice() throws ApiException {
        // First generate the invoice
        invoiceDto.generateInvoice(orderId);

        // Then test download
        String resource = invoiceDto.getPdfBase64(orderId);
        assertNotNull(resource);
        assertTrue(resource.length() > 0);
    }

    

    @Test
    void testRegenerateInvoice() throws ApiException {
        // Generate invoice first time
        String firstInvoicePath = invoiceDto.generateInvoice(orderId);
        assertNotNull(firstInvoicePath);

        // Generate invoice second time (should work)
        String secondInvoicePath = invoiceDto.generateInvoice(orderId);
        assertNotNull(secondInvoicePath);
    }
}