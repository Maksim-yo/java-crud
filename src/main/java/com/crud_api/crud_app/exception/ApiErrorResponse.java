package com.crud_api.crud_app.exception;

import lombok.NoArgsConstructor;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {
    private String message;
    private List<Violation> violations;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Violation {
        private String field;
        private String error;
    }
}