package com.increff.pos.dto;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.increff.pos.model.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.form.ClientForm;
import com.increff.pos.model.form.DataPageForm;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.model.form.OrderForm;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.model.form.PageForm;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.test.AbstractUnitTest;

public class OrderDtoTest extends AbstractUnitTest {

    @Autowired
    private OrderDto orderDto;

    @Autowired
    private ProductDto productDto;

    @Autowired
    private InventoryDto inventoryDto;

    @Autowired
    private ClientDto clientDto;


    private OrderForm orderForm;
    private String testBarcode;
    private String testClient;

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

        // Setup basic order form
        orderForm = new OrderForm();
        orderForm.setCustomerName("Test Customer");
        orderForm.setCustomerEmail("test@example.com");
        List<OrderItemForm> items = new ArrayList<>();
        OrderItemForm item = new OrderItemForm(testBarcode, 5, 90.0);
        items.add(item);
        orderForm.setOrderItems(items);
    }

    @Test
    void testAddOrder() throws ApiException {
        OrderData data = orderDto.addOrder(orderForm);
        assertNotNull(data);
        assertEquals(orderForm.getCustomerName(), data.getCustomerName());
        assertEquals(orderForm.getCustomerEmail(), data.getCustomerEmail());
        assertNotNull(data.getOrderId());
    
    }
   


    @Test
    void testGetOrderByOrderId() throws ApiException {
        // Add an order first
        OrderData addedOrder = orderDto.addOrder(orderForm);

        OrderData retrievedOrder = orderDto.getOrderByOrderId(addedOrder.getOrderId());
        assertNotNull(retrievedOrder);
        assertEquals(addedOrder.getOrderId(), retrievedOrder.getOrderId());
    }

    @Test
    void testGetAllOrdersWithPagination() throws ApiException {
        // Add an order first
        orderDto.addOrder(orderForm);

        PageForm pageForm = new PageForm();
        pageForm.setPage(0);
        pageForm.setSize(10);

        Page<OrderData> page = orderDto.getAllOrdersWithPagination(pageForm);
        assertNotNull(page);
        assertTrue(page.getTotalElements() > 0);
    }

    @Test
    void testAddOrderWithInvalidData() {
        // Test with empty customer name
        OrderForm invalidForm = new OrderForm();
        invalidForm.setCustomerName("");
        invalidForm.setCustomerEmail("test@example.com");
        invalidForm.setOrderItems(orderForm.getOrderItems());

        assertThrows(ApiException.class, () -> orderDto.addOrder(invalidForm));

        // Test with invalid email
        invalidForm.setCustomerName("Test Customer");
        invalidForm.setCustomerEmail("invalid-email");

        assertThrows(ApiException.class, () -> orderDto.addOrder(invalidForm));

        // Test with empty order items
        invalidForm.setCustomerEmail("test@example.com");
        invalidForm.setOrderItems(new ArrayList<>());

        assertThrows(ApiException.class, () -> orderDto.addOrder(invalidForm));
    }


    @Test
    void testCancelOrder() throws ApiException {
        // First create a fulfillable order
        OrderData addedOrder = orderDto.addOrder(orderForm);
        assertNotNull(addedOrder);
        assertEquals(OrderStatus.FULFILLABLE, addedOrder.getStatus());

        // Cancel the order
        OrderData cancelledOrder = orderDto.cancelOrder(addedOrder.getOrderId());
        
        // Verify the order was cancelled
        assertNotNull(cancelledOrder);
        assertEquals(OrderStatus.CANCELLED, cancelledOrder.getStatus());
        assertEquals(addedOrder.getOrderId(), cancelledOrder.getOrderId());
    }

}