package model;

import java.util.UUID;

public class AuthData {

    private final String authToken;
    private final String username;

    public AuthData(String username) {
       this.authToken = UUID.randomUUID().toString();
       this.username = username;
    }

//    public AuthData(String username, String authToken) {
//        this.username = username;
//        this.authToken = authToken;
//    }

    public String getAuthToken() {
        return authToken;
    }

    public String getUsername() {
        return username;
    }
}
