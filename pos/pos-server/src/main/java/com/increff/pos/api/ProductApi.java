package com.increff.pos.api;

import java.util.List;

import org.springframework.data.domain.Page;

import com.increff.pos.db.ProductPojo;
import com.increff.pos.exception.ApiException;

public interface ProductApi {
    ProductPojo add(ProductPojo productPojo) throws ApiException;
    Page<ProductPojo> getAll(Integer page, Integer size);
    ProductPojo update(ProductPojo pojo) throws ApiException;
    ProductPojo getByBarcode(String barcode) throws ApiException;
    ProductPojo getByProductID(String productID) throws ApiException;
    Page<ProductPojo> searchProducts(String searchPattern, String searchType, Integer page, Integer size) throws ApiException;
    List<String> getAllBarcodes() throws ApiException;
}   