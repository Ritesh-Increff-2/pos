package com.increff.pos.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.increff.pos.api.AuthApi;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.UserData;
import com.increff.pos.model.form.LoginForm;
import com.increff.pos.test.AbstractUnitTest;

public class AuthDtoTest extends AbstractUnitTest {

    @MockBean
    private AuthApi authApi;

    @Autowired
    private AuthDto authDto;

    // Test successful login
    @Test
    public void testLoginSuccess() throws ApiException {
        // Arrange
        LoginForm form = new LoginForm();
        form.setEmail("test@example.com");

        UserData expectedData = new UserData();
        expectedData.setEmail("test@example.com");
        expectedData.setRole("OPERATOR");

        when(authApi.login("test@example.com")).thenReturn(expectedData);

        // Act
        UserData result = authDto.login(form);

        // Assert
        assertEquals("test@example.com", result.getEmail());
        assertEquals("OPERATOR", result.getRole());
    }

    // Test login with invalid email format
    @Test
    public void testLoginInvalidEmail() {
        // Arrange
        LoginForm form = new LoginForm();
        form.setEmail("invalid-email");

        // Act & Assert
        assertThrows(ApiException.class, () -> authDto.login(form));
    }

    // Test login with empty email


    // Test login with null email
    @Test
    public void testLoginNullEmail() {
        // Arrange
        LoginForm form = new LoginForm();
        form.setEmail(null);

        // Act & Assert
        assertThrows(ApiException.class, () -> authDto.login(form));
    }
     
   

    
  
}