package com.increff.pos.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.increff.pos.api.flow.InventoryFlow;
import com.increff.pos.exception.ApiException;
import com.increff.pos.helper.InventoryHelper;
import com.increff.pos.model.data.InventoryData;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStream;
import org.springframework.http.HttpStatus;
import com.increff.pos.model.data.ErrorData;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.db.InventoryPojo;
import com.increff.pos.util.NormalizeUtil;
import com.increff.pos.util.ValidationUtil;

@Service
public class InventoryDto {
    @Autowired
    private InventoryFlow inventoryFlow;
    
    @Autowired
    private InventoryHelper inventoryHelper;

    public InventoryData addInventory(InventoryForm form) throws ApiException {
        ValidationUtil.validateInventoryForm(form);
        form.setBarcode(NormalizeUtil.normalize(form.getBarcode()));
        InventoryPojo pojo = inventoryFlow.addFlow(form.getBarcode(), form.getQuantity());
        return InventoryHelper.convertToData(pojo);
    }

    public InventoryData getInventoryByBarcode(String barcode) throws ApiException {
        ValidationUtil.validateBarcode(barcode);
        barcode = NormalizeUtil.normalize(barcode);
        InventoryPojo pojo = inventoryFlow.getByBarcodeFlow(barcode);
        return InventoryHelper.convertToData(pojo);
    }

    public ResponseEntity<?> addInventoryFromTsv(MultipartFile file) throws ApiException {
        ValidationUtil.validateTsvFile(file);
        try(InputStream inputStream = file.getInputStream()){
            List<InventoryForm> inventoryForms = inventoryHelper.parseTsvFile(inputStream);
            List<ErrorData> errorDataList = new ArrayList<>();
            ValidationUtil.validateInventoryFormList(inventoryForms);
            NormalizeUtil.normalizeInventoryFormList(inventoryForms);

            for(InventoryForm form : inventoryForms){
                try{
                    inventoryFlow.addFlow(form.getBarcode(),form.getQuantity());
                } catch(ApiException e){
                    errorDataList.add(new ErrorData(form.getBarcode(), e.getMessage()));
                }
            }
            if(!errorDataList.isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(inventoryHelper.generateErrorTsv(errorDataList));
            }
        } catch(Exception e){
            throw new ApiException("Error processing file: " + e.getMessage());
        }
        return ResponseEntity.ok("Inventory added successfully");
    }
} 