package com.noboru.webscraping.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class ErrorException extends RuntimeException{
    public ErrorException(String message) {
        super(message);
    }
}
