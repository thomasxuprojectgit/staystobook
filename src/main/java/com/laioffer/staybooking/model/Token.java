package com.laioffer.staybooking.model;

// we will not store token in database, we just check token from user
// or we create token to user
public class Token {
    private final String token;

    public Token(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}

