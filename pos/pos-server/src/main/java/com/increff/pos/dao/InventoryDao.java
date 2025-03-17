package com.increff.pos.dao;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.stereotype.Repository;

import com.increff.pos.db.InventoryPojo;

@Repository
public class InventoryDao extends AbstractDao<InventoryPojo> {
    
    public InventoryDao(MongoOperations mongoOperations) {
        super(
            new MongoRepositoryFactory(mongoOperations)
                .getEntityInformation(InventoryPojo.class),
            mongoOperations
        );
    }

    public InventoryPojo findByProductId(String productId) {
        return findByField("productId", productId);
    }
} 