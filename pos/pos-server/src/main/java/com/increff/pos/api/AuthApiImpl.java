package com.increff.pos.api;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.increff.pos.helper.TokenService;
import com.increff.pos.model.data.UserData;



@Service
public class AuthApiImpl implements AuthApi {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private Environment env;
 //TODO:: Create application file in config
    @Override
    public UserData login(String email){
        UserData userData = new UserData();
        userData.setEmail(email);
        
        List<String> supervisorList = Arrays.asList(env.getProperty("supervisor.emails").split(","));
        userData.setRole(supervisorList.contains(email) ? "supervisor" : "operator");
        String role = userData.getRole();
        return tokenService.generateToken(email,role);
    }
}