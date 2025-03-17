package com.increff.pos.api;

import java.util.List;

import org.springframework.data.domain.Page;

import com.increff.pos.db.ClientPojo;
import com.increff.pos.exception.ApiException;

public interface ClientApi {
    ClientPojo add(ClientPojo clientPojo) throws ApiException;
    ClientPojo update(String oldClientName, String newClientName) throws ApiException;
    Page<ClientPojo> getAll(int page, int size);
    List<ClientPojo> getAllWithoutPagination() throws ApiException;
    ClientPojo getByClientName(String clientName) throws ApiException;
} 