package com.increff.invoice.dao;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.stereotype.Service;

import com.increff.invoice.db.InvoicePojo;
@Service
public class InvoiceDao extends AbstractDao<InvoicePojo> {
    public InvoiceDao(MongoOperations mongoOperations) {
        super(
            new MongoRepositoryFactory(mongoOperations)
                .getEntityInformation(InvoicePojo.class),
            mongoOperations
        );
    }
    public InvoicePojo findByInvoiceId(String invoiceId) {
        Query query = Query.query(Criteria.where("invoiceId").is(invoiceId));
        return mongoOperations.findOne(query, InvoicePojo.class);
    }
}
