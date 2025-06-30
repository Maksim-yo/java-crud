package com.crud_api.crud_app.handlers;

import com.crud_api.crud_app.exception.ApiErrorResponse;
import com.crud_api.crud_app.exception.NotFoundException;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<ApiErrorResponse.Violation> violations = ex.getBindingResult()
                                                        .getFieldErrors()
                                                        .stream()
                                                        .map(error -> new ApiErrorResponse.Violation(error.getField(), error.getDefaultMessage()))
                                                        .toList();

        return new ApiErrorResponse("Validation failed", violations);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ApiErrorResponse handleConstraintViolation(ConstraintViolationException ex) {
        List<ApiErrorResponse.Violation> violations = ex.getConstraintViolations()
                                                        .stream()
                                                        .map(violation -> new ApiErrorResponse.Violation(violation.getPropertyPath().toString(), violation.getMessage()))
                                                        .toList();

        return new ApiErrorResponse("Validation failed", violations);
    }



    protected ApiErrorResponse  handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Throwable rootCause = ex.getMostSpecificCause();
        String friendlyMessage = "Invalid JSON format";
        JsonLocation location = null;

        if (rootCause instanceof UnrecognizedPropertyException unrecognizedEx) {
            String fieldName = unrecognizedEx.getPropertyName();
            friendlyMessage = "Unknown field in JSON: '" + fieldName + "'. Please check the request body.";
        } else if (rootCause instanceof JsonParseException parseEx) {
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
        log.warn("JSON parse error: {}", friendlyMessage, ex);
        return new ApiErrorResponse (friendlyMessage, null);
    }

    @ExceptionHandler({EntityNotFoundException.class, NotFoundException.class, EmptyResultDataAccessException.class})
    public ApiErrorResponse handleNotFound(Exception ex) {
        log.info("Not found: {}", ex.getMessage());
        return new ApiErrorResponse("Not found: " + ex.getMessage(), null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ApiErrorResponse handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation", ex);
        return new ApiErrorResponse(
                "Database error: referential integrity violation: non-existent reference to another table", null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ApiErrorResponse handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        String msg = "Type mismatch: parameter '" + ex.getName() + "' should be " + requiredType;
        log.warn(msg);
        return new ApiErrorResponse(msg, null);
    }

    protected ApiErrorResponse handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String msg = "Missing request parameter: " + ex.getParameterName();
        log.warn(msg);
        return new ApiErrorResponse(msg, null);
    }

    protected ApiErrorResponse handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                         HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String msg = "HTTP method not allowed: " + ex.getMethod();
        log.warn(msg);
        return new ApiErrorResponse(msg, null);
    }

    @ExceptionHandler(Exception.class)
    public ApiErrorResponse handleGenericException(Exception ex) {
        log.error("Unhandled exception", ex);
        return new ApiErrorResponse("Internal server error. Please contact support.", null);
    }
}
