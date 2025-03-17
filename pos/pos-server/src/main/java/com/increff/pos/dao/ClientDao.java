package com.increff.pos.dao;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.stereotype.Repository;

import com.increff.pos.db.ClientPojo;

@Repository
public class ClientDao extends AbstractDao<ClientPojo> {
    public ClientDao(MongoOperations mongoOperations) {
        super(
            new MongoRepositoryFactory(mongoOperations)
                .getEntityInformation(ClientPojo.class),
            mongoOperations
        );
    }
    public ClientPojo findByName(String name) {
        return findByField("name", name);
    }
    
}