package com.increff.pos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.increff.pos.dto.AuthDto;
import com.increff.pos.model.data.UserData;
import com.increff.pos.model.form.LoginForm;
import com.increff.pos.exception.ApiException;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthDto authDto;

    @PostMapping("/login")
    public UserData login(@RequestBody LoginForm form) throws ApiException {
        return authDto.login(form);
    }
} 