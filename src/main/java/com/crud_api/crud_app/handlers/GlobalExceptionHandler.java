package com.crud_api.crud_app.handlers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.crud_api.crud_app.exception.ErrorResponse;
import com.crud_api.crud_app.exception.ValidationErrorResponse;
import com.crud_api.crud_app.exception.ValidationErrorResponse.Violation;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.core.JsonParseException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.crud_api.crud_app.exception.NotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Обработка ошибок валидации @Valid на @RequestBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {

        List<ValidationErrorResponse.Violation> violations = ex.getBindingResult().getFieldErrors().stream()
        .map(error -> new ValidationErrorResponse.Violation(error.getField(), error.getDefaultMessage()))
        .toList();

        ValidationErrorResponse response = new ValidationErrorResponse("Validation failed", violations);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Обработка ошибок ConstraintViolation (например, при валидации @PathVariable, @RequestParam)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        List<ValidationErrorResponse.Violation> violations = ex.getConstraintViolations().stream()
            .map(violation -> new ValidationErrorResponse.Violation(
                violation.getPropertyPath().toString(),
                violation.getMessage()
            ))
            .toList();

        ValidationErrorResponse response = new ValidationErrorResponse("Validation failed", violations);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

     @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("Not found: " + ex.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(NotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = ex.getRootCause() != null ? ex.getRootCause().getMessage() : "Data integrity violation";
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(new ErrorResponse("Database error: referential integrity violation: non-existent reference to another table"));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(HttpMessageNotReadableException ex) {
        Throwable rootCause = ex.getMostSpecificCause();

        String friendlyMessage = "Invalid JSON format";
        JsonLocation location = null;

        if (rootCause instanceof JsonParseException parseEx) {
            location = parseEx.getLocation();
            friendlyMessage = "Malformed JSON";
      } else if (rootCause instanceof JsonMappingException mappingEx) {
            location = mappingEx.getLocation();
            friendlyMessage = "Type mismatch or invalid structure in JSON";

            String message = rootCause.getMessage();

            Pattern pattern = Pattern.compile("Cannot deserialize value of type `(.*?)`");
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                String fullType = matcher.group(1);
                String simpleType = fullType.substring(fullType.lastIndexOf('.') + 1);
                friendlyMessage += ", expected type: " + simpleType;
            }

            if (!mappingEx.getPath().isEmpty()) {
                String fieldName = mappingEx.getPath().get(mappingEx.getPath().size() - 1).getFieldName();
                friendlyMessage += " in field '" + fieldName + "'";
            }
        }

        if (location != null) {
            friendlyMessage += String.format(" at line %d, column %d", location.getLineNr(), location.getColumnNr());
        }

        return ResponseEntity.badRequest().body(new ErrorResponse(friendlyMessage));
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        return ResponseEntity
            .badRequest()
            .body(new ErrorResponse("Type mismatch: parameter '" + ex.getName() + "' should be " + requiredType));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex) {
        return ResponseEntity
            .badRequest()
            .body(new ErrorResponse("Missing request parameter: " + ex.getParameterName()));
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity
            .status(HttpStatus.METHOD_NOT_ALLOWED)
            .body(new ErrorResponse("HTTP method not allowed: " + ex.getMethod()));
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDeleteNotFound(EmptyResultDataAccessException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("The employee to delete was not found"));
    }

    // Обработка всех остальных исключений
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ex.printStackTrace(); // можно логировать
        return new ResponseEntity<>(
            new ErrorResponse("Internal server error: " + ex.getMessage()),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}