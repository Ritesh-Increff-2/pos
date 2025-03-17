package com.increff.pos.helper;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import com.increff.pos.db.ProductPojo;
import com.increff.pos.model.data.ErrorData;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;

@Service
public class ProductHelper {
    public static ProductPojo convertToEntity(ProductForm form) {
        ProductPojo pojo = new ProductPojo();
        pojo.setCreatedAt(ZonedDateTime.now());
        pojo.setBarcode(form.getBarcode().trim().toLowerCase());
        pojo.setClientName(form.getClientName().trim().toLowerCase());
        pojo.setProductName(form.getProductName().trim().toLowerCase());
        pojo.setMrp(form.getMrp());
        pojo.setImageUrl(form.getImageUrl());
        pojo.setUpdatedAt(ZonedDateTime.now());
        return pojo;
    }
    public static ProductData convertToData(ProductPojo pojo) {
        ProductData data = new ProductData();
        data.setId(pojo.getId());
        data.setBarcode(pojo.getBarcode());
        data.setClientName(pojo.getClientName());
        data.setProductName(pojo.getProductName());
        data.setMrp(pojo.getMrp());
        data.setImageUrl(pojo.getImageUrl());
        return data;
    }
    public static List<ProductForm> parseTsvFile(InputStream inputStream) throws Exception {
        List<ProductForm> productForms = new ArrayList<>();
        try (CSVParser parser = new CSVParser(new InputStreamReader(inputStream), CSVFormat.TDF.withFirstRecordAsHeader())) {
            for (CSVRecord record : parser) {
                ProductForm form = new ProductForm();
                form.setBarcode(record.get("barcode").trim());
                form.setClientName(record.get("clientName").trim());
                form.setProductName(record.get("productName").trim());
                form.setMrp(Double.parseDouble(record.get("mrp")));
                form.setImageUrl(record.get("imageUrl").trim());
                productForms.add(form);
            }
        }
        return productForms;
    }

    public static String generateErrorTsv(List<ErrorData> errorDataList) {
        StringBuilder sb = new StringBuilder();
        sb.append("barcode\terror\n");
        for (ErrorData errorData : errorDataList) {
            sb.append(errorData.getBarcode()).append("\t").append(errorData.getError()).append("\n");
        }
        return sb.toString();
    }
} 