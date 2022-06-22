package com.laioffer.staybooking.controller;

import com.laioffer.staybooking.exception.GCSUploadException;
import com.laioffer.staybooking.exception.StayNotExistException;
import com.laioffer.staybooking.exception.UserAlreadyExistException;
import com.laioffer.staybooking.exception.UserNotExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * @ControllerAdvice AOP, important function in Spring
 * AOP is a programming paradigm that aims to increase modularity by allowing the separation of cross-cutting concerns.
 * It does this by adding additional behavior to existing code without modifying the code itself.
 */
@ControllerAdvice
public class CustomExceptionHandler {

    // WebRequest not used
    // based on different exception, use this handler to process and return to user
    @ExceptionHandler(UserAlreadyExistException.class)
    public final ResponseEntity<String> handleUserAlreadyExistExceptions(Exception ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotExistException.class)
    public final ResponseEntity<String> handleUserNotExistExceptions(Exception ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(StayNotExistException.class)
    public final ResponseEntity<String> handleStayNotExistExceptions(Exception ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(GCSUploadException.class)
    public final ResponseEntity<String> handleGCSUploadExceptions(Exception ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


}

