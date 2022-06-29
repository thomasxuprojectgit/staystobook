package com.laioffer.staybooking.service;

import com.laioffer.staybooking.exception.StayDeleteException;
import com.laioffer.staybooking.exception.StayNotExistException;
import com.laioffer.staybooking.repository.StayReservationDateRepository;
import org.springframework.stereotype.Service;

import com.laioffer.staybooking.repository.StayRepository;
import org.springframework.beans.factory.annotation.Autowired;

import com.laioffer.staybooking.model.*;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.multipart.MultipartFile;

import com.laioffer.staybooking.repository.ReservationRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.laioffer.staybooking.repository.LocationRepository;


/**
 * CRUD Stay info between front end and database
 */
@Service
public class StayService {
    private StayRepository stayRepository;

    private LocationRepository locationRepository;

    private ReservationRepository reservationRepository;

    // use ImageStorageService as StayService's dependency
    private ImageStorageService imageStorageService;

    private GeoCodingService geoCodingService;


    // for updating stay_reserved_date table
    private StayReservationDateRepository stayReservationDateRepository;


    // StayRepository is used to communicate with database
    @Autowired
    public StayService(StayRepository stayRepository, LocationRepository locationRepository,
                       ReservationRepository reservationRepository,
                       ImageStorageService imageStorageService, GeoCodingService geoCodingService,
                       StayReservationDateRepository stayReservationDateRepository) {
        this.stayRepository = stayRepository;
        this.locationRepository = locationRepository;
        this.reservationRepository = reservationRepository;
        this.imageStorageService = imageStorageService;
        this.geoCodingService = geoCodingService;
        this.stayReservationDateRepository = stayReservationDateRepository;

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

        // Get Location from Google API
        Location location = geoCodingService.getLatLng(stay.getId(), stay.getAddress());

        // Save location to ElasticSearch
        locationRepository.save(location);

    }

    // delete a stay
    // @Transactional make sure atomic of a transaction
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(Long stayId, String username) throws StayNotExistException {
        Stay stay = stayRepository.findByIdAndHost(stayId, new User.Builder().setUsername(username).build());
        if (stay == null) {
            throw new StayNotExistException("Stay doesn't exist");
        }

        // find any booked reservation dates after now
        List<Reservation> reservations = reservationRepository.findByStayAndCheckoutDateAfter(stay, LocalDate.now());

        // if found the dates booked, can not delete this stay, throw exception
        if (reservations != null && reservations.size() > 0) {
            throw new StayDeleteException("Cannot delete stay with active reservation");
        }

        // if not found any dates booked, can delete, also, we need to delete the reserved dates, per stay ID
        List<StayReservedDate> stayReservedDates = stayReservationDateRepository.findByStay(stay);

        for(StayReservedDate date : stayReservedDates) {
            stayReservationDateRepository.deleteById(date.getId());
        }

        // can also use stayRepository.deleteByStay(stay);
        stayRepository.deleteById(stayId);
    }



}

