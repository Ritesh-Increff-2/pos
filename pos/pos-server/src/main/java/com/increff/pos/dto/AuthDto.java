package com.increff.pos.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.pos.api.AuthApi;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.UserData;
import com.increff.pos.model.form.LoginForm;
import com.increff.pos.util.ValidationUtil;

@Service
public class AuthDto {
    @Autowired
    private AuthApi authApi;

    public UserData login(LoginForm form) throws ApiException {
        ValidationUtil.validateEmail(form.getEmail());
        return authApi.login(form.getEmail());
    }
} 