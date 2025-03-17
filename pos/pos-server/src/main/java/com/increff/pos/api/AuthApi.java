package com.increff.pos.api;

import com.increff.pos.model.data.UserData;

public interface AuthApi {
    UserData login(String email);
}
