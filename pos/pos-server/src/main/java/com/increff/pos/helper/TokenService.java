package com.increff.pos.helper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.Objects;
import org.springframework.stereotype.Service;
 
import com.increff.pos.model.data.UserData;


@Service
public class TokenService {
    private static final HashSet<UserData> tokenSet = new HashSet<>();

    private static final Set<String> SUPERVISOR_ONLY_PATHS = new HashSet<>(Arrays.asList(
        "/api/product/add",
        "/api/product/add-csv",
        "/api/inventory",
        "/api/inventory/add-csv",
        "/api/client"

        ));

    public static UserData generateToken(String email, String role) {
        String token = UUID.randomUUID().toString();
        UserData userData = new UserData();
        userData.setEmail(email);
        userData.setRole(role);
        userData.setToken(token);
        tokenSet.add(userData);
        return userData;
    }

    public static UserData validateToken(String token , String path,String method) {
      if(path.isEmpty() && method.equals("GET")) {
        System.out.println("Warning : Using Default validateToken method");
      }

      UserData userData = null;
      for(UserData user : tokenSet){
        if(user.getToken().equals(token)){
          userData = user;
          break;
        }
      }
      if(Objects.isNull(userData)){
        return null;
      }

      if(SUPERVISOR_ONLY_PATHS.contains(path) && !userData.getRole().equals("supervisor")){
        System.out.println("Warning : User is not a supervisor");
        return null;
      }
      return userData;
    }

    public UserData validateToken(String token){
        return validateToken(token,"","");  
    }

    public void removeToken(String token){
        tokenSet.removeIf(user -> user.getToken().equals(token));
    }

}