package com.laioffer.staybooking.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;

import io.jsonwebtoken.Claims;

/**
 * Class for token
 * JWT is jsonwebtoken
 */
@Component
public class JwtUtil {

    // get secret from application.properties
    @Value("${jwt.secret}")
    private String secret;

    // generate token
    // token can store hashmap of key value pair(for validation later)
    // use setIssuedAt and setExpiration to set start time and expiration time
    // signWith - sign in method
    // subject is username, 可以通过subject反向找token
    // when user login later, user needs to carries token to login
    public String generateToken(String subject) {
        return Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    private Claims extractClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    // get username from token
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public Date extractExpiration(String token) {
        return extractClaims(token).getExpiration();
    }

    // Method to valid whether token is valid or not
    // if token pass the time, invalid
    public Boolean validateToken(String token) {
        return extractExpiration(token).after(new Date());
    }


}

