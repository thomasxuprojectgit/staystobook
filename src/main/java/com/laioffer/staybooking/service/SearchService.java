package com.laioffer.staybooking.service;

import org.springframework.stereotype.Service;
import com.laioffer.staybooking.repository.LocationRepository;
import com.laioffer.staybooking.repository.StayReservationDateRepository;
import com.laioffer.staybooking.repository.StayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.laioffer.staybooking.model.Stay;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Service
public class SearchService {

    // use to search stay table from MySQL
    private StayRepository stayRepository;

    // use to search StayReservationDate table from MySQL
    private StayReservationDateRepository stayReservationDateRepository;

    // use to search elasticsearch database
    private LocationRepository locationRepository;

    @Autowired
    public SearchService(StayRepository stayRepository, StayReservationDateRepository stayReservationDateRepository, LocationRepository locationRepository) {
        this.stayRepository = stayRepository;
        this.stayReservationDateRepository = stayReservationDateRepository;
        this.locationRepository = locationRepository;
    }

    // use this method to do search Stays around certain lat,lon and needed dates
    public List<Stay> search(int guestNumber, LocalDate checkinDate, LocalDate checkoutDate, double lat, double lon, String distance) {

        // use elastic search to locate stays per lat, lon, distance
        List<Long> stayIds = locationRepository.searchByDistance(lat, lon, distance);

        // if not find any stay, just return empty stays
        if (stayIds == null || stayIds.isEmpty()) {
            return new ArrayList<>();
        }

        // Get stay ids that dates/stays that has been reserved
        Set<Long> reservedStayIds = stayReservationDateRepository.findByIdInAndDateBetween(stayIds, checkinDate, checkoutDate.minusDays(1));

        // filter out dates/stays that has been reserved, get list of stay ids
        List<Long> filteredStayIds = new ArrayList<>();
        for (Long stayId : stayIds) {
            if (!reservedStayIds.contains(stayId)) {
                filteredStayIds.add(stayId);
            }
        }

        // filter out stay ids that the availble guest number is not enough for the needs
        return stayRepository.findByIdInAndGuestNumberGreaterThanEqual(filteredStayIds, guestNumber);
    }


}

