package com.laioffer.staybooking.service;

import com.laioffer.staybooking.exception.StayNotExistException;
import org.springframework.stereotype.Service;

import com.laioffer.staybooking.repository.StayRepository;
import org.springframework.beans.factory.annotation.Autowired;

import com.laioffer.staybooking.model.*;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CRUD Stay info between front end and database
 */
@Service
public class StayService {
    private StayRepository stayRepository;

    // use ImageStorageService as StayService's dependency
    private ImageStorageService imageStorageService;

    // StayRepository is used to communicate with database
    @Autowired
    public StayService(StayRepository stayRepository, ImageStorageService imageStorageService) {
        this.stayRepository = stayRepository;
        this.imageStorageService = imageStorageService;

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

    // add a stay standard info, and add the image of the stay
    // make sure updates to two tables (StayImage table and Stay table) is atomic
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void add(Stay stay, MultipartFile[] images) {
        // parallel() parallel upload images
        // upload images and get links of images
        List<String> mediaLinks = Arrays.stream(images).parallel().map(image -> imageStorageService.save(image)).collect(Collectors.toList());
        List<StayImage> stayImages = new ArrayList<>();

        // add each image link to StayImage table
        for (String mediaLink : mediaLinks) {
            stayImages.add(new StayImage(mediaLink, stay));
        }
        stay.setImages(stayImages);

        // add Stay obj to Stay table
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

