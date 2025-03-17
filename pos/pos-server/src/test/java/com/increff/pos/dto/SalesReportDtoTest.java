package com.increff.pos.dto;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.SalesReportData;
import com.increff.pos.model.form.ClientForm;
import com.increff.pos.model.form.DateFilterForm;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.model.form.OrderForm;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.test.AbstractUnitTest;

public class SalesReportDtoTest extends AbstractUnitTest {
    
    @Autowired
    private SalesReportDto salesReportDto;
    
    @Autowired
    private OrderDto orderDto;
    
    @Autowired
    private ProductDto productDto;
    
    @Autowired
    private InventoryDto inventoryDto;
    
    @Autowired
    private ClientDto clientDto;

    @Autowired
    private InvoiceDto invoiceDto;

    private String testBarcode;
    private String testClient;

    @BeforeEach
    void setUp() throws ApiException {
        // Create test client
        testClient = "test client2";
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
        createTestOrder();
    }

    private void createTestOrder() throws ApiException {
        OrderForm orderForm = new OrderForm();
        List<OrderItemForm> items = new ArrayList<>();
        OrderItemForm item = new OrderItemForm("TEST123", 5, 90.0);
        items.add(item);
        orderForm.setOrderItems(items);
        orderForm.setCustomerName("Test Customer");
        orderForm.setCustomerEmail("test@example.com");
        OrderData data = orderDto.addOrder(orderForm);

        String orderId = data.getOrderId();
        try {
            invoiceDto.generateInvoice(orderId);
        } catch (ApiException e) {
            fail("Failed");
        }
    }

    @Test
    void testGetSalesReport() throws ApiException {
        DateFilterForm form = new DateFilterForm();
        form.setStartDate(ZonedDateTime.now().minusDays(1));
        form.setEndDate(ZonedDateTime.now().plusDays(1));
        
        List<SalesReportData> report = salesReportDto.getSalesReport(form, testClient);
        
        assertNotNull(report);
        assertFalse(report.isEmpty());
        SalesReportData data = report.get(0);
        assertEquals(testClient, data.getClientName());
        assertTrue(data.getRevenue() > 0);
        assertTrue(data.getQuantity() > 0);
    }

    @Test
    void testGetSalesReportWithInvalidDates() {
        DateFilterForm form = new DateFilterForm();
        form.setStartDate(ZonedDateTime.now().plusDays(1));
        form.setEndDate(ZonedDateTime.now().minusDays(1));
        
        assertThrows(ApiException.class, () -> salesReportDto.getSalesReport(form, testClient));
    }

    @Test
    void testGetSalesReportWithNonExistentClient() throws ApiException {
        DateFilterForm form = new DateFilterForm();
        form.setStartDate(ZonedDateTime.now().minusDays(1));
        form.setEndDate(ZonedDateTime.now().plusDays(1));
        
        List<SalesReportData> report = salesReportDto.getSalesReport(form, "non-existent-client");
        assertTrue(report.isEmpty());
    }
}