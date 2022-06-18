package com.laioffer.staybooking.controller;

import com.laioffer.staybooking.exception.UserAlreadyExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * @ControllerAdvice AOP, important function in Spring
 */
@ControllerAdvice
public class CustomExceptionHandler {

    // WebRequest not used
    // based on different exception, use this handler to process and return to user
    @ExceptionHandler(UserAlreadyExistException.class)
    public final ResponseEntity<String> handleUserAlreadyExistExceptions(Exception ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }
}

