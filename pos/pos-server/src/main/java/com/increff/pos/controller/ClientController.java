package com.increff.pos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.increff.pos.dto.ClientDto;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.ClientData;
import com.increff.pos.model.form.ClientForm;
import com.increff.pos.model.form.ClientUpdateForm;
import com.increff.pos.model.form.PageForm;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
@CrossOrigin(origins = "*")


@Tag(name = "Client Management", description = "APIs for managing clients")
@RestController
@RequestMapping("/api/client")
public class ClientController {
    @Autowired
    private ClientDto clientDto;

    @Operation(summary = "Add a new client")
    @RequestMapping(path = "", method = RequestMethod.POST)
    public ClientData addClient(@RequestBody ClientForm form) throws ApiException {
        return clientDto.addClient(form);
       
    }

    @Operation(summary = "Get all clients with pagination")
    @RequestMapping(path = "/get", method = RequestMethod.POST)
    public Page<ClientData> getAllClientWithPagination(@RequestBody PageForm form) throws ApiException {
        return clientDto.getAllClientWithPagination(form);
    }

    @Operation(summary = "Update client name")
    @RequestMapping(path = "", method = RequestMethod.PUT)
    public ClientData updateClientName(@RequestBody ClientUpdateForm form) throws ApiException {
        return clientDto.updateClientName(form);
    }

    @Operation(summary = "Get all clients without pagination")
    @RequestMapping(path = "", method = RequestMethod.GET)
    public List<ClientData> getAllWithoutPagination() throws ApiException {
        return clientDto.getAllWithoutPagination();
    }
}