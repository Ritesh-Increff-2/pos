package com.increff.pos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.increff.pos.dto.InventoryDto;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.form.InventoryForm;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
@CrossOrigin(origins = "*")
@Tag(name = "Inventory Management", description = "APIs for managing inventory")
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryDto inventoryDto;

    @Operation(summary = "Add inventory")
    @RequestMapping(path = "", method = RequestMethod.POST)
    public InventoryData addInventory(@RequestBody InventoryForm inventoryForm) throws ApiException {
        return inventoryDto.addInventory(inventoryForm);
    }

    @Operation(summary = "Get inventory by barcode")
    @RequestMapping(path = "/{barcode}", method = RequestMethod.GET)
    public InventoryData getInventoryByBarcode(@PathVariable String barcode) throws ApiException {
        return inventoryDto.getInventoryByBarcode(barcode);
    }

    @Operation(summary = "Add Inventory from TSV File")
    @RequestMapping(path = "/tsv", method = RequestMethod.POST)
    public ResponseEntity<?> addInventoryFromTsv(@RequestParam("file") MultipartFile file) throws ApiException {
        return inventoryDto.addInventoryFromTsv(file);
    }
    
} 