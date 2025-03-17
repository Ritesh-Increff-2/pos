package com.increff.pos.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import com.increff.pos.db.InventoryPojo;
import com.increff.pos.db.ProductPojo;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.ErrorData;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.util.ValidationUtil;
import com.increff.pos.model.form.InventoryForm;

@Service
public class InventoryHelper {

    public static InventoryPojo convertToEntity(String barcode, int quantity, String productId) {
        InventoryPojo pojo = new InventoryPojo();
        pojo.setCreatedAt(ZonedDateTime.now());
        pojo.setProductId(productId);
        pojo.setQuantity(quantity);
        pojo.setUpdatedAt(ZonedDateTime.now());
        return pojo;
    }

    public static InventoryData convertToData(InventoryPojo pojo) {
        InventoryData data = new InventoryData();
        data.setId(pojo.getId());
        data.setQuantity(pojo.getQuantity());
        return data;
    }
    public List<InventoryForm> parseTsvFile(InputStream inputStream) throws ApiException {
        List<InventoryForm> inventoryForms = new ArrayList<>();
        try (CSVParser parser = new CSVParser(new InputStreamReader(inputStream), CSVFormat.TDF.withFirstRecordAsHeader())) {
            for (CSVRecord record : parser) {
                String barcode = record.get("barcode").trim();
                String quantityStr = record.get("quantity").trim();
                ValidationUtil.validateBarcode(barcode);
                ValidationUtil.validateQuantity(quantityStr);
                    int quantity = Integer.parseInt(quantityStr);
                    InventoryForm form = new InventoryForm();
                    form.setBarcode(barcode);
                    form.setQuantity(quantity);
                    inventoryForms.add(form);
            }
        } catch (IOException e) {
            throw new ApiException("Error reading TSV file: " + e.getMessage());
        }
        return inventoryForms;
    }
    public String generateErrorTsv(List<ErrorData> errorDataList) {
        StringBuilder sb = new StringBuilder();
        sb.append("barcode\terror\n");
        for (ErrorData errorData : errorDataList) {
            sb.append(errorData.getBarcode()).append("\t").append(errorData.getError()).append("\n");
        }
        return sb.toString();
    }
} 