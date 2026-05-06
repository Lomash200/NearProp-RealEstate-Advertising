package com.nearprop.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthException extends RuntimeException {
    private final HttpStatus status;

    public AuthException(String message) {
        this(message, HttpStatus.BAD_REQUEST);
    }

    public AuthException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
} 