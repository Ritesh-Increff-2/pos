package com.increff.pos.api.flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.increff.pos.api.ClientApi;
import com.increff.pos.api.ProductApi;
import com.increff.pos.api.InventoryApi;
import com.increff.pos.db.ProductPojo;
import com.increff.pos.db.InventoryPojo;
import com.increff.pos.exception.ApiException;
import com.increff.pos.helper.ProductHelper;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.model.data.FullProductData;

@Service
@Transactional(rollbackFor = Exception.class)
public class ProductFlow {

    @Autowired
    private ProductApi productApi;

    @Autowired
    private ClientApi clientApi;

    @Autowired
    private InventoryApi inventoryApi;

    public ProductPojo add(ProductForm form) throws ApiException {
        clientApi.getByClientName(form.getClientName());
        ProductPojo pojo = ProductHelper.convertToEntity(form);
        return productApi.add(pojo);
    }

    public Page<FullProductData> getAllWithPagination(Integer page, Integer size) throws ApiException {
        Page<ProductPojo> productPage = productApi.getAll(page, size);
        
        return productPage.map(product -> {
            FullProductData fullProductData = new FullProductData();
            fullProductData.setBarcode(product.getBarcode());
            fullProductData.setClientName(product.getClientName());
            fullProductData.setProductName(product.getProductName());
            fullProductData.setMrp(product.getMrp());
            fullProductData.setImageUrl(product.getImageUrl());
            
            try {
                InventoryPojo inventory = inventoryApi.getcheckByProductId(product.getId());
                fullProductData.setInventoryQuantity(inventory != null ? inventory.getQuantity() : 0);
            } catch (ApiException e) {
                fullProductData.setInventoryQuantity(0);
            }
            
            return fullProductData;
        });
    }
} 