package com.example.Booking.Exception;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {

        super(message);
    }
}