package com.laioffer.staybooking.service;

import com.laioffer.staybooking.exception.StayNotExistException;
import org.springframework.stereotype.Service;

import com.laioffer.staybooking.repository.StayRepository;
import org.springframework.beans.factory.annotation.Autowired;

import com.laioffer.staybooking.model.*;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * CRUD Stay info between front end and database
 */
@Service
public class StayService {
    private StayRepository stayRepository;

    // StayRepository is used to communicate with database
    @Autowired
    public StayService(StayRepository stayRepository) {
        this.stayRepository = stayRepository;
    }

    // search stays of a user
    public List<Stay> listByUser(String username) {
        // per given user the Builder in User class to create a user before find this user's stay
        return stayRepository.findByHost(new User.Builder().setUsername(username).build());
    }

    // search a stay of a user, per id and username
    public Stay findByIdAndHost(Long stayId, String username) throws StayNotExistException {
        Stay stay = stayRepository.findByIdAndHost(stayId, new User.Builder().setUsername(username).build());
        if (stay == null) {
            throw new StayNotExistException("Stay doesn't exist");
        }
        return stay;
    }

    // add a stay
    public void add(Stay stay) {
        stayRepository.save(stay);
    }

    // delete a stay
    // @Transactional make sure atomic of a transaction
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(Long stayId, String username) throws StayNotExistException {
        Stay stay = stayRepository.findByIdAndHost(stayId, new User.Builder().setUsername(username).build());
        if (stay == null) {
            throw new StayNotExistException("Stay doesn't exist");
        }
        // can also use stayRepository.deleteByStay(stay);
        stayRepository.deleteById(stayId);
    }



}

