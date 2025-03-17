package com.increff.pos.dto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ClientForm;
import com.increff.pos.model.form.PageForm;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.model.form.SearchForm;
import com.increff.pos.test.AbstractUnitTest;

public class ProductDtoTest extends AbstractUnitTest {

    @Autowired
    private ProductDto productDto;

    @Autowired
    private ClientDto clientDto;

    private String testClient;
    private ProductForm productForm;

    @BeforeEach
    void setUp() throws ApiException {
        // Create test client
        testClient = "test client";
        ClientForm clientForm = new ClientForm();
        clientForm.setName(testClient);
        clientDto.addClient(clientForm);

        // Setup basic product form
        productForm = new ProductForm();
        productForm.setBarcode("TEST123");
        productForm.setClientName(testClient);
        productForm.setProductName("Test Product");
        productForm.setMrp(100.0);
        productForm.setImageUrl("http://example.com/test.jpg");
    }

    @Test
    void testAddProduct() throws ApiException {
        ProductData data = productDto.addProduct(productForm);
        assertNotNull(data);
        assertEquals(productForm.getBarcode(), data.getBarcode());
        assertEquals(productForm.getProductName(), data.getProductName());
    }

    @Test
    void testGetAllProductWithPagination() throws ApiException {
        productDto.addProduct(productForm);
        // Test pagination
        PageForm pageForm = new PageForm();
        pageForm.setPage(0);
        pageForm.setSize(10);
        
        Page<ProductData> page = productDto.getAllProductWithPagination(pageForm);
        assertNotNull(page);
        assertTrue(page.getTotalElements() > 0);
    }

    @Test
    void testUpdateProduct() throws ApiException {
        productDto.addProduct(productForm);
        ProductForm updateForm = new ProductForm();
        updateForm.setBarcode(productForm.getBarcode());
        updateForm.setClientName(testClient);
        updateForm.setProductName("Updated Product");
        updateForm.setMrp(150.0);
        updateForm.setImageUrl("http://example.com/updated.jpg");

        ProductData updated = productDto.updateProduct(updateForm);
        assertEquals("Updated Product", updated.getProductName());
        assertEquals(150.0, updated.getMrp());
    }
    @Test
    void testAddProductsFromValidTsv() throws ApiException {
        String tsvContent = "barcode\tclientName\tproductName\tmrp\timageUrl\n" +
                           "TSV123\tTSV Product\t" + testClient + "\t200.0\thttp://example.com/tsv.jpg";

        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.tsv",
            "text/tab-separated-values",
            tsvContent.getBytes()
        );

        ResponseEntity<?> response = productDto.addProductsFromTsv(file);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testAddProductsFromInvalidTsv() {
        String invalidTsvContent = "invalid\tformat\tfile\n" +
                                 "TSV123\tIncomplete\tData";

        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.tsv",
            "text/tab-separated-values",
            invalidTsvContent.getBytes()
        );

        assertThrows(ApiException.class, () -> productDto.addProductsFromTsv(file));
    }

    @Test
    void testGetAllBarcodes() throws ApiException {
        // Add a product first
        productDto.addProduct(productForm);

        List<String> barcodes = productDto.getAllBarcodes();
        assertNotNull(barcodes);
        assertTrue(barcodes.contains(productForm.getBarcode()));
    }

    @Test
    void testDuplicateBarcode() {
        // Try to add product with same barcode
        assertThrows(ApiException.class, () -> {
            productDto.addProduct(productForm);
            productDto.addProduct(productForm);
        });
    }

    @Test
    void testInvalidMrp() {
        productForm.setMrp(-100.0);
        assertThrows(ApiException.class, () -> productDto.addProduct(productForm));
    }
    @Test
    void testGetProductByProductID() throws ApiException {
        // Add test product
        ProductData addedProduct = productDto.addProduct(productForm);

        // Get product by ID
        ProductData retrievedProduct = productDto.getProductByProductID(addedProduct.getId());
        assertNotNull(retrievedProduct);
        assertEquals(addedProduct.getId(), retrievedProduct.getId());
    }
}