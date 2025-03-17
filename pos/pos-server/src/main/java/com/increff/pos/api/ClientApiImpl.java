package com.increff.pos.api;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.increff.pos.dao.ClientDao;
import com.increff.pos.db.ClientPojo;
import com.increff.pos.exception.ApiException;

@Service
public class ClientApiImpl implements ClientApi {

    @Autowired
    private ClientDao dao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClientPojo add(ClientPojo pojo) throws ApiException {
        ClientPojo existingClient = dao.findByName(pojo.getName());
        if (Objects.nonNull(existingClient)) {
            throw new ApiException("Client with name '" + pojo.getName() + "' already exists");
        }
        return dao.save(pojo);
    }
    
    @Override
    public Page<ClientPojo> getAll(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return dao.findAll(pageRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClientPojo update(String oldClientName, String newClientName) throws ApiException {
        ClientPojo existingClientPojo = dao.findByName(oldClientName);
        if (Objects.isNull(existingClientPojo)) {
            throw new ApiException("Client not found with name: " + oldClientName);
        }
        existingClientPojo.setName(newClientName);
        existingClientPojo.setUpdatedAt(ZonedDateTime.now());
        return dao.save(existingClientPojo);
    }
    
    @Override
    public List<ClientPojo> getAllWithoutPagination() throws ApiException {
        return dao.findAll();
    }

    @Override
    public ClientPojo getByClientName(String clientName) throws ApiException {
        ClientPojo client = dao.findByName(clientName);
        if (Objects.isNull(client)) {
            throw new ApiException("Client not found with name: " + clientName);
        }
        return client;
    }
}
