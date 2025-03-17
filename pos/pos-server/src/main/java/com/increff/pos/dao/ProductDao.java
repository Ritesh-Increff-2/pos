package com.increff.pos.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.stereotype.Repository;

import com.increff.pos.db.ProductPojo;

@Repository
public class ProductDao extends AbstractDao<ProductPojo> {

    public ProductDao(MongoOperations mongoOperations) {
        super(
            new MongoRepositoryFactory(mongoOperations)
                .getEntityInformation(ProductPojo.class),
            mongoOperations
        );
    }

    public ProductPojo findByBarcode(String barcode) {
        return findByField("barcode", barcode);
    }

    public ProductPojo findByProductID(String productID) {
        return findByField("_id", productID);
    }

    public Page<ProductPojo> searchProductsByProductName(String searchPattern, PageRequest pageRequest) {
        return searchByFieldPattern("productName", searchPattern, pageRequest);
    }

    public Page<ProductPojo> searchProductsByClientName(String searchPattern, PageRequest pageRequest) {
        return searchByFieldPattern("clientName", searchPattern, pageRequest);
    }

    public List<String> getAllBarcodes() {
        Query query = Query.query(Criteria.where("barcode").exists(true));
        List<ProductPojo> products = mongoOperations.find(query, ProductPojo.class);
        return products.stream()
                .map(ProductPojo::getBarcode)
                .collect(Collectors.toList());
    }
} 