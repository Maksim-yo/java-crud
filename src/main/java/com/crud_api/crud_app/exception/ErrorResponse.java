package com.crud_api.crud_app.exception;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private String message;
    private LocalDateTime timestamp;

    public ErrorResponse(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

}