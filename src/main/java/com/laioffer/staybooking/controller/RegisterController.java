package com.laioffer.staybooking.controller;

import org.springframework.web.bind.annotation.RestController;
import com.laioffer.staybooking.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;

import com.laioffer.staybooking.model.User;
import com.laioffer.staybooking.model.UserRole;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



/**
 * @RestController combine @Controller and @ResponseBody
 */
@RestController
public class RegisterController {
    private RegisterService registerService;

    @Autowired
    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    // @PostMapping map to a URL with post method
    // @RequestBody convert json response from front end to User obj
    // UserRole.ROLE_GUEST enum make sure only two type, others will not work
    // did not catch error from exception from service, use CustomExceptionHandler to handle exception
    @PostMapping("/register/guest")
    public void addGuest(@RequestBody User user) {
        // get user obj (converted from json) to database by calling registerService.add
        registerService.add(user, UserRole.ROLE_GUEST);
    }

    // same logic as addGuest
    @PostMapping("/register/host")
    public void addHost(@RequestBody User user) {
        registerService.add(user, UserRole.ROLE_HOST);
    }




}

