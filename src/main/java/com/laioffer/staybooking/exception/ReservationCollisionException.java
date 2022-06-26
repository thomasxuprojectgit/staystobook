package com.laioffer.staybooking.exception;

/**
 * If reserved the stay and date have been booked, throw exception
 */
public class ReservationCollisionException extends RuntimeException {
    public ReservationCollisionException(String message) {
        super(message);
    }
}

