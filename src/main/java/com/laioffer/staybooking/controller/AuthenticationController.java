package com.laioffer.staybooking.controller;

import org.springframework.web.bind.annotation.RestController;

import com.laioffer.staybooking.model.Token;
import com.laioffer.staybooking.model.User;
import com.laioffer.staybooking.model.UserRole;
import com.laioffer.staybooking.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class AuthenticationController {
    private AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    // method to authenticate guest
    // user send username, password, which will be converted to User obj through @RequestBody (Spring framework -
    // Deserialize from json to obj)
    // use method authenticationService.authenticate method to generate token
    // authenticationService will call function in SecurityConfig(by using AuthenticationManagerBuilder to complete the
    // actual authentication work)
    @PostMapping("/authenticate/guest")
    public Token authenticateGuest(@RequestBody User user) {
        return authenticationService.authenticate(user, UserRole.ROLE_GUEST);
    }

    // method to authenticate host
    @PostMapping("/authenticate/host")
    public Token authenticateHost(@RequestBody User user) {
        return authenticationService.authenticate(user, UserRole.ROLE_HOST);
    }
}


