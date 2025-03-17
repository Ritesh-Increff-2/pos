package com.increff.pos.db;

import java.time.ZonedDateTime;

import org.springframework.data.annotation.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractPojo {
    @Id
    private String id;
    private ZonedDateTime createdAt ;
    private ZonedDateTime updatedAt;
   
} 
