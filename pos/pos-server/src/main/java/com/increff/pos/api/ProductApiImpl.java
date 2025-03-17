package com.increff.pos.api;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.increff.pos.dao.ProductDao;
import com.increff.pos.db.ProductPojo;
import com.increff.pos.exception.ApiException;
@Service
public class ProductApiImpl implements ProductApi {

    @Autowired
    private ProductDao productDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductPojo add(ProductPojo productPojo) throws ApiException {
        checkIfBarcodeExists(productPojo.getBarcode());
        return productDao.save(productPojo);
    }

    @Override
    public Page<ProductPojo> getAll(Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return productDao.findAll(pageRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductPojo update(ProductPojo pojo) throws ApiException {
        ProductPojo existingProduct = productDao.findByBarcode(pojo.getBarcode());
        if (Objects.isNull(existingProduct)) {
            throw new ApiException("No product exists with barcode: " + pojo.getBarcode());
        }
        existingProduct.setProductName(pojo.getProductName());
        existingProduct.setMrp(pojo.getMrp());
        existingProduct.setImageUrl(pojo.getImageUrl());
        existingProduct.setUpdatedAt(ZonedDateTime.now());
        return productDao.save(existingProduct);
    }

    @Override
    public ProductPojo getByBarcode(String barcode) throws ApiException {
        ProductPojo product = productDao.findByBarcode(barcode);
        if (Objects.isNull(product)) {
            throw new ApiException("No product exists with barcode: " + barcode);
        }
        return product;
    }

    @Override
    public ProductPojo getByProductID(String productID) throws ApiException {
        return productDao.findByProductID(productID);
    }

    private void checkIfBarcodeExists(String barcode) throws ApiException {
        ProductPojo existingProductPojo = productDao.findByBarcode(barcode);
        if(Objects.nonNull(existingProductPojo)) {
            throw new ApiException("Product already exists with barcode: " + barcode);
        }
    }

    @Override
    public Page<ProductPojo> searchProducts(String searchPattern, String searchType, Integer page, Integer size) throws ApiException {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        if (Objects.isNull(searchPattern) || searchPattern.trim().isEmpty()) {
            return productDao.findAll(pageRequest);
        }

        if ("client".equalsIgnoreCase(searchType)) {
            return productDao.searchProductsByClientName(searchPattern, pageRequest);
        } else {
            return productDao.searchProductsByProductName(searchPattern, pageRequest);
        }
    }

    @Override
    public List<String> getAllBarcodes() throws ApiException {
        return productDao.getAllBarcodes();
    }
    
} 
