package com.laioffer.staybooking.exception;

/**
 * give exception when user already exists when register
 */
public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException(String message) {
        super(message);
    }
}

