package com.crud_api.crud_app.exception;

import java.time.LocalDateTime;
import java.util.List;

import com.crud_api.crud_app.exception.ValidationErrorResponse.Violation;

import lombok.Getter;

@Getter
public class ValidationErrorResponse {
    private final List<Violation> errors;
    private final LocalDateTime timestamp;
    private final String message;

    public ValidationErrorResponse(String message, List<Violation> errors) {
        this.errors = errors;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    @Getter
    public static class Violation {
        private final String field;
        private final String message;

        public Violation(String field, String message) {
            this.field = field;
            this.message = message;
        }

    }

}
