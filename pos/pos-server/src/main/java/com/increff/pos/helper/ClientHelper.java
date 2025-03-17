package com.increff.pos.helper;

import java.time.ZonedDateTime;

import com.increff.pos.db.ClientPojo;
import com.increff.pos.model.data.ClientData;
import com.increff.pos.model.form.ClientForm;

public class ClientHelper {
    public static ClientPojo convertToEntity(ClientForm form) {
        ClientPojo pojo = new ClientPojo();
        pojo.setName(form.getName());
        pojo.setCreatedAt(ZonedDateTime.now());
        pojo.setUpdatedAt(ZonedDateTime.now());
        return pojo;
    }

    public static ClientData convertToData(ClientPojo pojo) {
        ClientData data = new ClientData();
        data.setId(pojo.getId());
        data.setName(pojo.getName());
        return data;
    }
} 