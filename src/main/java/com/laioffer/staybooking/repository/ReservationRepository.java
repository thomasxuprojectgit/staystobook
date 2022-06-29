package com.laioffer.staybooking.repository;

import com.laioffer.staybooking.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.laioffer.staybooking.model.Stay;
import com.laioffer.staybooking.model.User;

import java.time.LocalDate;
import java.util.List;


/**
 * Use to get Reservation from database
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByGuest(User guest);

    List<Reservation> findByStay(Stay stay);

    // Find a reservation per stay id and guest user id
    Reservation findByIdAndGuest(Long id, User guest); // for deletion, if the reservation not belong to this user, deletion will fail

    // If the stay has some reservation after specific date, we can not delete this stay
    List<Reservation> findByStayAndCheckoutDateAfter(Stay stay, LocalDate date);

}

