package com.increff.pos.dto;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.increff.pos.api.ClientApi;
import com.increff.pos.db.ClientPojo;
import com.increff.pos.exception.ApiException;
import com.increff.pos.helper.ClientHelper;
import com.increff.pos.model.data.ClientData;
import com.increff.pos.model.form.ClientForm;
import com.increff.pos.model.form.ClientUpdateForm;
import com.increff.pos.model.form.PageForm;
import com.increff.pos.util.NormalizeUtil;
import com.increff.pos.util.ValidationUtil;

@Service
public class ClientDto {
    @Autowired
    private ClientApi clientApi;


    public ClientData addClient(ClientForm form) throws ApiException {
        ValidationUtil.validateClientForm(form);
//        form.setName(NormalizeUtil.normalize(form.getName()));
        NormalizeUtil.normalize(form);
        ClientPojo pojo = ClientHelper.convertToEntity(form);
        ClientPojo savedPojo = clientApi.add(pojo);
        return ClientHelper.convertToData(savedPojo);
    }

    public Page<ClientData> getAllClientWithPagination(PageForm form) throws ApiException {
        ValidationUtil.validatePageForm(form);
        Page<ClientPojo> clientPage = clientApi.getAll(form.getPage(), form.getSize());
        return clientPage.map(ClientHelper::convertToData);
    }

    public ClientData updateClientName(ClientUpdateForm form) throws ApiException {
        ValidationUtil.validateClientUpdateForm(form);
        form.setNewClientName(NormalizeUtil.normalize(form.getNewClientName()));
        ClientPojo updatedClient = clientApi.update(form.getOldClientName(), form.getNewClientName());
        return ClientHelper.convertToData(updatedClient);
    }

    public List<ClientData> getAllWithoutPagination() throws ApiException {
        List<ClientPojo> clientPojos = clientApi.getAllWithoutPagination();
        return clientPojos.stream()
            .map(ClientHelper::convertToData)
            .collect(Collectors.toList());
    }

} 
