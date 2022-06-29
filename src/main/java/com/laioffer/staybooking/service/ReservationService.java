package com.laioffer.staybooking.service;

import com.laioffer.staybooking.model.*;
import org.springframework.stereotype.Service;

import com.laioffer.staybooking.repository.ReservationRepository;
import com.laioffer.staybooking.repository.StayReservationDateRepository;
import org.springframework.beans.factory.annotation.Autowired;

import com.laioffer.staybooking.exception.ReservationCollisionException;
import com.laioffer.staybooking.exception.ReservationNotFoundException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


@Service
public class ReservationService {
    private ReservationRepository reservationRepository;

    // Will provide booked stays and dates
    private StayReservationDateRepository stayReservationDateRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, StayReservationDateRepository stayReservationDateRepository) {
        this.reservationRepository = reservationRepository;
        this.stayReservationDateRepository = stayReservationDateRepository;
    }

    // Get reservations per the User obj
    public List<Reservation> listByGuest(String username) {
        return reservationRepository.findByGuest(new User.Builder().setUsername(username).build());
    }

    // Get reservations per the Stay obj
    public List<Reservation> listByStay(Long stayId) {
        return reservationRepository.findByStay(new Stay.Builder().setId(stayId).build());
    }

    // Add reservation
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void add(Reservation reservation) throws ReservationCollisionException {

        // Per guest provided reservation date range and stay id, check with database (ReservationDates table) to see
        // whether these dates for this stay has been booked for some days (then put in set)
        Set<Long> stayIds = stayReservationDateRepository.findByIdInAndDateBetween(Arrays.asList(reservation.getStay().getId()), reservation.getCheckinDate(), reservation.getCheckoutDate().minusDays(1));

        // if set of stay is not empty, there is duplicate(between guest choice and database data(reserved by others)), throw exception
        if (!stayIds.isEmpty()) {
            throw new ReservationCollisionException("Duplicate reservation");
        }

        // For saving reversed dates
        List<StayReservedDate> reservedDates = new ArrayList<>();

        // Save every date to the list of reservedDates, one day by one day by using for loop
        for (LocalDate date = reservation.getCheckinDate(); date.isBefore(reservation.getCheckoutDate()); date = date.plusDays(1)) {
            reservedDates.add(new StayReservedDate(new StayReservedDateKey(reservation.getStay().getId(), date), reservation.getStay()));
        }

        // save the list of reserved dates to Stay table
        stayReservationDateRepository.saveAll(reservedDates);

        // Save the Reservation obj to Reservation table
        reservationRepository.save(reservation);
    }

    // To delete reservation,  need both the username and reservationId, to make sure no delete reservation of others
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(Long reservationId, String username) {
        Reservation reservation = reservationRepository.findByIdAndGuest(reservationId, new User.Builder().setUsername(username).build());

        // if can not find reservation, throw error
        if (reservation == null) {
            throw new ReservationNotFoundException("Reservation is not available");
        }

        // iterate over each date
        for (LocalDate date = reservation.getCheckinDate(); date.isBefore(reservation.getCheckoutDate()); date = date.plusDays(1)) {

            // delete each date's reservation from table stay_reserved_date, id is composite primary key (StayReservedDateKey)
            stayReservationDateRepository.deleteById(new StayReservedDateKey(reservation.getStay().getId(), date));
        }

        // delete from table reservation
        reservationRepository.deleteById(reservationId);
    }

}

