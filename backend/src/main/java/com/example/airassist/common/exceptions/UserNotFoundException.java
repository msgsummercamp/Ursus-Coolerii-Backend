package com.example.airassist.common.exceptions;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends UserException{
    public UserNotFoundException(String message, HttpStatus status) {
        super(message, status);
    }
}
