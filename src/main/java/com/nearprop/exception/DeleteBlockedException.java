package com.nearprop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // 409 Conflict
public class DeleteBlockedException extends RuntimeException {

    public DeleteBlockedException(String message) {
        super(message);
    }

    // Optional: agar stack trace nahi chahiye
    public DeleteBlockedException(String message, Throwable cause) {
        super(message, cause);
    }
}