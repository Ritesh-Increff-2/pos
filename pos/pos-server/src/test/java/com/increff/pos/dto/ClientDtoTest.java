package com.increff.pos.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.increff.pos.test.AbstractUnitTest;
import com.increff.pos.api.ClientApi;
import com.increff.pos.db.ClientPojo;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.ClientData;
import com.increff.pos.model.form.ClientForm;
import com.increff.pos.model.form.ClientUpdateForm;
import com.increff.pos.model.form.PageForm;

public class ClientDtoTest extends AbstractUnitTest {

    @MockBean
    private ClientApi clientApi;

    @Autowired
    private ClientDto clientDto;

    // Test adding a client with valid data
    @Test
    public void testAddClientSuccess() throws ApiException {
        // Arrange
        ClientForm form = new ClientForm();
        form.setName("Test Client");

        ClientPojo expectedPojo = new ClientPojo();
        expectedPojo.setId("123");
        expectedPojo.setName("test client");

        when(clientApi.add(any(ClientPojo.class))).thenReturn(expectedPojo);

        // Act
        ClientData result = clientDto.addClient(form);

        // Assert
        assertEquals("123", result.getId());
        assertEquals("test client", result.getName());
    }

    // Test adding a client with empty name
    @Test
    public void testAddClientEmptyName() {
        // Arrange
        ClientForm form = new ClientForm();
        form.setName("");

        // Act & Assert
        assertThrows(ApiException.class, () -> clientDto.addClient(form));
    }

    // Test adding a client with null name


    // Test getting a client by name
    @Test
    public void testGetClientSuccess() throws ApiException {
        // Arrange
        String clientName = "Test Client";
        ClientPojo pojo = new ClientPojo();
        pojo.setId("123");
        pojo.setName("test client");

        when(clientApi.getByClientName("test client")).thenReturn(pojo);

        // Act
        ClientData result = clientDto.getClient(clientName);

        // Assert
        assertEquals("123", result.getId());
        assertEquals("test client", result.getName());
    }

    // Test getting a client with non-existent name
   @Test
   public void testGetClientNotFound() {
       // Arrange
       String clientName = "Non Existent";
       try {
           when(clientApi.getByClientName(any())).thenThrow(new ApiException("Client not found"));
       } catch (ApiException e) {
           // Ignore the exception in mock setup
       }

       // Act & Assert
       assertThrows(ApiException.class, () -> clientDto.getClient(clientName));
   }

    // Test getting all clients with pagination
    @Test
    public void testGetAllClientWithPagination() throws ApiException {
        // Arrange
        PageForm form = new PageForm();
        form.setPage(0);
        form.setSize(10);

        List<ClientPojo> clients = Arrays.asList(
            createClientPojo("1", "Client 1"),
            createClientPojo("2", "Client 2")
        );
        Page<ClientPojo> clientPage = new PageImpl<>(clients);

        when(clientApi.getAll(0, 10)).thenReturn(clientPage);

        // Act
        Page<ClientData> result = clientDto.getAllClientWithPagination(form);

        // Assert
        assertEquals(2, result.getContent().size());
        assertEquals("Client 1", result.getContent().get(0).getName());
        assertEquals("Client 2", result.getContent().get(1).getName());
    }

    // Test updating client name
    @Test
    public void testUpdateClientNameSuccess() throws ApiException {
        // Arrange
        ClientUpdateForm form = new ClientUpdateForm();
        form.setOldClientName("Old Client");
        form.setNewClientName("New Client");

        ClientPojo updatedPojo = new ClientPojo();
        updatedPojo.setId("123");
        updatedPojo.setName("new client");

        when(clientApi.update("old client", "new client")).thenReturn(updatedPojo);

        // Act
        ClientData result = clientDto.updateClientName(form);

        // Assert
        assertEquals("123", result.getId());
        assertEquals("new client", result.getName());
    }

    // Test updating client with empty new name
    @Test
    public void testUpdateClientEmptyNewName() {
        // Arrange
        ClientUpdateForm form = new ClientUpdateForm();
        form.setOldClientName("Old Client");
        form.setNewClientName("");

        // Act & Assert
        assertThrows(ApiException.class, () -> clientDto.updateClientName(form));
    }

    // Test getting all clients without pagination
    @Test
    public void testGetAllWithoutPagination() throws ApiException {
        // Arrange
        List<ClientPojo> clients = Arrays.asList(
            createClientPojo("1", "Client 1"),
            createClientPojo("2", "Client 2")
        );

        when(clientApi.getAllWithoutPagination()).thenReturn(clients);

        // Act
        List<ClientData> result = clientDto.getAllWithoutPagination();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Client 1", result.get(0).getName());
        assertEquals("Client 2", result.get(1).getName());
    }

    // Test pagination with invalid page number
    @Test
    public void testGetAllClientWithInvalidPagination() {
        // Arrange
        PageForm form = new PageForm();
        form.setPage(-1);
        form.setSize(10);

        // Act & Assert
        assertThrows(ApiException.class, () -> clientDto.getAllClientWithPagination(form));
    }

    // Helper method to create ClientPojo
    private ClientPojo createClientPojo(String id, String name) {
        ClientPojo pojo = new ClientPojo();
        pojo.setId(id);
        pojo.setName(name);
        return pojo;
    }
}