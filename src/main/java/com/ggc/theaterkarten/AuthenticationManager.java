package com.ggc.theaterkarten;

import java.util.HashMap;
import java.util.Map;

public class AuthenticationManager {
    private Map<String, String> userCredentials;

    public AuthenticationManager() {
        userCredentials = new HashMap<>();
    }

    public void registerUser(String username, String password) {
        if (userCredentials.containsKey(username)) {
            throw new IllegalArgumentException("Username already exists. Please choose a different username.");
        }

        userCredentials.put(username, password);
    }

    public boolean authenticateUser(String username, String password) {
        String storedPassword = userCredentials.get(username);
        return storedPassword != null && storedPassword.equals(password);
    }
}