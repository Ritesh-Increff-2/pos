package com.increff.pos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.increff.pos.dto.ProductDto;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.data.FullProductData;
import com.increff.pos.model.form.PageForm;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.model.form.SearchForm;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@Tag(name = "Product Management", description = "APIs for managing products")
@RestController
@RequestMapping("/api/product")
public class ProductController {
    @Autowired
    private ProductDto productDto;

    @Operation(summary = "Add a new product")
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    public ProductData addProduct(@RequestBody ProductForm form) throws ApiException {
        return productDto.addProduct(form);
    }

    @Operation(summary = "Get all products with pagination")
    @RequestMapping(path = "/get-all", method = RequestMethod.POST)
    public Page<FullProductData> getAllProductWithPagination(@RequestBody PageForm form) throws ApiException {
        return productDto.getAllProductWithPagination(form);
    }

    @Operation(summary = "Update product by barcode")
    @RequestMapping(path = "", method = RequestMethod.PUT)
    public ProductData updateProduct(@RequestBody ProductForm form) throws ApiException {
        return productDto.updateProduct(form);
    }

    @Operation(summary = "get product by productID")
    @RequestMapping(path = "/{productID}", method = RequestMethod.GET)
    public ProductData getProductByProductID(@PathVariable String productID) throws ApiException {
        return productDto.getProductByProductID(productID);
    }
    
    @Operation(summary = "Add products from TSV file")
    @RequestMapping(path = "/tsv", method = RequestMethod.POST)
    public ResponseEntity<?> addProductsFromTsv(@RequestParam("file") MultipartFile file) throws ApiException {
        return productDto.addProductsFromTsv(file);
    }

    @Operation(summary = "Search products by product name or client name")
    @RequestMapping(path = "/search", method = RequestMethod.POST)
    public Page<ProductData> searchProducts(@RequestBody(required = false) SearchForm form) throws ApiException {
        return productDto.searchProducts(form);
    }

    @Operation(summary = "Get list of all barcodes")
    @RequestMapping(path = "/allBarcodes",method = RequestMethod.GET)
    public List<String> getAllBarcodes() throws ApiException {
        return productDto.getAllBarcodes();
    }
} 