package com.laioffer.staybooking.service;
import com.laioffer.staybooking.model.UserRole;
import com.laioffer.staybooking.repository.AuthorityRepository;
import com.laioffer.staybooking.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.laioffer.staybooking.model.Authority;
import com.laioffer.staybooking.model.User;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.laioffer.staybooking.exception.UserAlreadyExistException;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * used for registration
 */
@Service
public class RegisterService {

    // this field is obj, UserRepository has been implemented as obj,  as implementation has been auto created in the userRepository
    // use autowired below to do injection
    private UserRepository userRepository;
    // this field is obj, AuthorityRepository has been implemented as obj,  as implementation has been auto created in the authorityRepository
    // use autowired below to do injection
    private AuthorityRepository authorityRepository;

    private PasswordEncoder passwordEncoder;


    // see comment above
    @Autowired
    public RegisterService(UserRepository userRepository, AuthorityRepository authorityRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // @Transactional make sure the atomic of a transaction(auto rollback if fail)
    // data class methoned isolation level, SERIALIZABLE is highest
    // exception will throws to caller (controller)
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void add(User user, UserRole role) throws UserAlreadyExistException {
        // .existsById (method by Spring Data JPA) check whether primary key exist in database
        if (userRepository.existsById(user.getUsername())) {
            throw new UserAlreadyExistException("User already exists");
        }

        // encode password first, and save to user obj
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // set enable field to true for this user obj
        user.setEnabled(true);

        // save user info in database
        userRepository.save(user);

        // save user authority to database
        //                                                           .name: convert Enum to string
        authorityRepository.save(new Authority(user.getUsername(), role.name()));
    }



}

