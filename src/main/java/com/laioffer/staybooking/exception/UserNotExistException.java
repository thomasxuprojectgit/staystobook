package com.laioffer.staybooking.exception;

/**
 * if user not exist, throw error
 */
public class UserNotExistException extends RuntimeException {
    public UserNotExistException(String message) {
        super(message);
    }
}

