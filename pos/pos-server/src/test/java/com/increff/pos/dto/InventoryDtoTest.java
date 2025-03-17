package com.increff.pos.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;



import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.test.AbstractUnitTest;
import com.increff.pos.model.form.ClientForm;

public class InventoryDtoTest extends AbstractUnitTest {

    @Autowired
    private InventoryDto inventoryDto;

    @Autowired
    private ProductDto productDto;

    @Autowired
    private ClientDto clientDto;

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
        testBarcode = "test123";
        ProductForm productForm = new ProductForm();
        productForm.setBarcode(testBarcode);
        productForm.setClientName(testClient);
        productForm.setProductName("Test Product");
        productForm.setMrp(100.0);
        productForm.setImageUrl("http://example.com/test.jpg");
        productDto.addProduct(productForm);
    }

    @Test
    void testAddInventory() throws ApiException {
        InventoryForm form = new InventoryForm();
        form.setBarcode(testBarcode);
        form.setQuantity(100);

        InventoryData data = inventoryDto.addInventory(form);

        assertNotNull(data);
        assertEquals(testBarcode, data.getBarcode());
        assertEquals(100, data.getQuantity());
    }

    @Test
    void testGetInventory() throws ApiException {
        // First add inventory
        InventoryForm form = new InventoryForm();
        form.setBarcode(testBarcode);
        form.setQuantity(100);
        inventoryDto.addInventory(form);

        // Then get it
        InventoryData data = inventoryDto.getInventoryByBarcode(testBarcode);
        assertNotNull(data);
        assertEquals(testBarcode, data.getBarcode());
        assertEquals(100, data.getQuantity());
    }

    @Test
    void testUpdateInventory() throws ApiException {
        // First add inventory
        InventoryForm form = new InventoryForm();
        form.setBarcode(testBarcode);
        form.setQuantity(100);
        inventoryDto.addInventory(form);

        // Update inventory
        form.setQuantity(200);
        inventoryDto.updateInventoryByBarcode(form);

        // Verify update
        InventoryData data = inventoryDto.getInventoryByBarcode(testBarcode);
        assertEquals(200, data.getQuantity());
    }

    @Test
    void testAddInventoryFromTsv() throws ApiException {
        // Create TSV content
        String tsvContent = "barcode\tquantity\n" + 
                          testBarcode + "\t100\n";

        // Create mock MultipartFile
        MultipartFile file = new MockMultipartFile(
            "file",
            "test.tsv",
            "text/tab-separated-values",
            tsvContent.getBytes(StandardCharsets.UTF_8)
        );

        // Test file upload
        ResponseEntity<?> response = inventoryDto.addInventoryFromTsv(file);
        assertEquals(200, response.getStatusCodeValue());
        
        // Verify inventory was added
        InventoryData data = inventoryDto.getInventoryByBarcode(testBarcode);
        assertNotNull(data);
        assertEquals(100, data.getQuantity());
    }

    @Test
    void testInvalidBarcode() {
        InventoryForm form = new InventoryForm();
        form.setBarcode("");  // Invalid empty barcode
        form.setQuantity(100);

        assertThrows(ApiException.class, () -> inventoryDto.addInventory(form));
    }

    @Test
    void testInvalidQuantity() {
        InventoryForm form = new InventoryForm();
        form.setBarcode(testBarcode);
        form.setQuantity(-1);  // Invalid negative quantity

        assertThrows(ApiException.class, () -> inventoryDto.addInventory(form));
    }
}