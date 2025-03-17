package com.increff.pos.db;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@CompoundIndex(name = "idx_name", def = "{'name': 1}", unique = true)
@Document(collection = "clients")
public class ClientPojo extends AbstractPojo {
    private String name;
}