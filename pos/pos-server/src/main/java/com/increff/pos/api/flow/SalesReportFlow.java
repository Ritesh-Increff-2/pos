package com.increff.pos.api.flow;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import java.util.Objects;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.ConvertOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.increff.pos.model.data.SalesReportData;
import com.increff.pos.util.NormalizeUtil;

@Service
public class SalesReportFlow {
    private final MongoTemplate mongoOperations;

    @Autowired
    public SalesReportFlow(MongoTemplate mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    public List<SalesReportData> getSalesReport(ZonedDateTime startDate, ZonedDateTime endDate, String clientName) {
        try {
            Criteria criteria = Criteria.where("status").is("INVOICED")
                .and("orderTime").gte(startDate.toInstant()).lte(endDate.toInstant());

            List<AggregationOperation> operations = new ArrayList<>();
            operations.add(Aggregation.match(criteria));
            operations.add(Aggregation.unwind("orderItems"));
            
            operations.add(Aggregation.addFields()
                .addFieldWithValue("productObjectId", 
                    ConvertOperators.ToObjectId.toObjectId("$orderItems.productId"))
                .build());

            operations.add(Aggregation.lookup("products", "productObjectId", "_id", "product"));
            operations.add(Aggregation.unwind("product"));

            // Add client filter if clientName is provided
            if (Objects.nonNull(clientName) && !clientName.trim().isEmpty()) {
                String normalizedClientName = NormalizeUtil.normalize(clientName);
                operations.add(Aggregation.match(Criteria.where("product.clientName").is(normalizedClientName)));
            }

            operations.add(Aggregation.group("product._id")
                .first("product.clientName").as("clientName")
                .first("product.productName").as("productName")
                .first("orderItems.sellingPrice").as("sellingPrice")
                .sum("orderItems.quantity").as("quantity")
                .sum("orderItems.orderItemTotal").as("revenue"));

            operations.add(Aggregation.project()
                .andExclude("_id")
                .and("clientName").as("clientName")
                .and("productName").as("productName")
                .and("sellingPrice").as("sellingPrice")
                .and("quantity").as("quantity")
                .and("revenue").as("revenue"));

            operations.add(Aggregation.sort(Sort.by(Sort.Order.asc("clientName"), Sort.Order.asc("productName"))));

            Aggregation aggregation = Aggregation.newAggregation(operations);
            List<Document> results = mongoOperations.aggregate(aggregation, "orders", Document.class).getMappedResults();
            return convertListDocumentToListSalesReportData(results);
        } catch (Exception e) {
            throw new RuntimeException("Error in sales report: " + e.getMessage(), e);
        }
    }

    private List<SalesReportData> convertListDocumentToListSalesReportData(List<Document> results) {
        return results.stream().map(doc -> {
            try {
                SalesReportData data = new SalesReportData();
                data.setClientName(doc.getString("clientName"));
                data.setProductName(doc.getString("productName"));
                
                // Safely convert numeric values
                data.setQuantity(convertToDouble(doc.get("quantity")));
                data.setSellingPrice(convertToDouble(doc.get("sellingPrice")));
                data.setRevenue(convertToDouble(doc.get("revenue")));
                
                return data;
            } catch (Exception e) {
                throw new RuntimeException("Error converting document: " + e.getMessage(), e);
            }
        }).collect(Collectors.toList());
    }

    // Helper method to safely convert numeric values to Double
    private Double convertToDouble(Object value) {
        if (Objects.isNull(value)) {
            return 0.0;
        }
        if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        }
        if (value instanceof Long) {
            return ((Long) value).doubleValue();
        }
        if (value instanceof Double) {
            return (Double) value;
        }
        return Double.valueOf(value.toString());
    }
}
