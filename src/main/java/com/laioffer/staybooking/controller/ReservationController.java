package com.laioffer.staybooking.controller;

import org.springframework.web.bind.annotation.RestController;

import com.laioffer.staybooking.exception.InvalidReservationDateException;
import com.laioffer.staybooking.model.Reservation;
import com.laioffer.staybooking.model.User;
import com.laioffer.staybooking.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import com.laioffer.staybooking.model.Reservation;
import com.laioffer.staybooking.service.ReservationService;


/**
 * For reservation
 */
@RestController
public class ReservationController {
    private ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }


    // get list of reservation per guest username
    // @GetMapping default is Get method
    @GetMapping(value = "/reservations")
    public List<Reservation> listReservations(Principal principal) {
        return reservationService.listByGuest(principal.getName());
    }

    // add reservation
    @PostMapping("/reservations")
    public void addReservation(@RequestBody Reservation reservation, Principal principal) {

        // get check in and check out dates
        LocalDate checkinDate = reservation.getCheckinDate();
        LocalDate checkoutDate = reservation.getCheckoutDate();

        // if check in/ check out dates are not valid, throw exception
        if (checkinDate.equals(checkoutDate) || checkinDate.isAfter(checkoutDate) || checkinDate.isBefore(LocalDate.now())) {
            throw new InvalidReservationDateException("Invalid date for reservation");
        }

        // set gust of this reservation
        reservation.setGuest(new User.Builder().setUsername(principal.getName()).build());

        // add the reservation
        reservationService.add(reservation);
    }

    // delete  a reservation per reservation ID and guest username
    @DeleteMapping("/reservations/{reservationId}")
    public void deleteReservation(@PathVariable Long reservationId, Principal principal) {
        reservationService.delete(reservationId, principal.getName());
    }

}
