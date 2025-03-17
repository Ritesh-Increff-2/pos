package com.increff.pos.dto;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.increff.pos.api.ProductApi;
import com.increff.pos.api.flow.ProductFlow;
import com.increff.pos.db.ProductPojo;
import com.increff.pos.exception.ApiException;
import com.increff.pos.helper.ProductHelper;
import com.increff.pos.model.data.FullProductData;
import org.springframework.http.HttpStatus;
import java.io.InputStream;
import com.increff.pos.model.data.ProductData;
import java.util.ArrayList;
import com.increff.pos.model.data.ErrorData;
import com.increff.pos.model.form.PageForm;

import com.increff.pos.model.form.ProductForm;
import com.increff.pos.model.form.SearchForm;
import com.increff.pos.util.NormalizeUtil;
import com.increff.pos.util.ValidationUtil;

@Service
public class ProductDto {
    @Autowired
    private ProductApi productApi;

    @Autowired
    private ProductFlow productFlow;


    public ProductData addProduct(ProductForm form) throws ApiException {
        ValidationUtil.validateProductForm(form);
        NormalizeUtil.normalizeProductForm(form);
        ProductPojo pojo = productFlow.add(form);
        return ProductHelper.convertToData(pojo);
    }

    public Page<FullProductData> getAllProductWithPagination(PageForm form) throws ApiException {
        ValidationUtil.validatePageForm(form);
        return productFlow.getAllWithPagination(form.getPage(), form.getSize());
    }

    public ProductData updateProduct(ProductForm form) throws ApiException {
        ValidationUtil.validateProductForm(form);
        NormalizeUtil.normalizeProductForm(form);
        ProductPojo pojo = ProductHelper.convertToEntity(form);
        ProductPojo updatedPojo = productApi.update(pojo);
        return ProductHelper.convertToData(updatedPojo);
    }

    public ProductData getProductByProductID(String productID) throws ApiException {
        ProductPojo productPojo = productApi.getByProductID(productID);
        return ProductHelper.convertToData(productPojo);
    }
    
    public ResponseEntity<?> addProductsFromTsv(MultipartFile file) throws ApiException {
        ValidationUtil.validateTsvFile(file);
        try (InputStream inputStream = file.getInputStream()) {
            List<ProductForm> productForms = ProductHelper.parseTsvFile(inputStream);
            List<ErrorData> errorDataList = new ArrayList<>();

            ValidationUtil.validateProductFormList(productForms);
            NormalizeUtil.normalizeProductFormList(productForms);


            for (ProductForm form : productForms) {
                try {
                    productFlow.add(form);
                } catch (ApiException e) {
                    errorDataList.add(new ErrorData(form.getBarcode(), e.getMessage()));
                }   
            }
            if (!errorDataList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ProductHelper.generateErrorTsv(errorDataList));
            }

            
        } catch (Exception e) {
            throw new ApiException("Error processing file: " + e.getMessage());
        }
        return ResponseEntity.ok("Products added successfully");
    }

    public Page<ProductData> searchProducts(SearchForm form) throws ApiException {
        Page<ProductPojo> products = productApi.searchProducts(
            form.getSearchPattern(),
            form.getSearchType(),
            form.getPage(),
            form.getSize()
        );
        return products.map(ProductHelper::convertToData);
    }

    public List<String> getAllBarcodes() throws ApiException {
        return productApi.getAllBarcodes();
    }
}   