package com.crud_api.crud_app.handlers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.crud_api.crud_app.exception.ErrorResponse;
import com.crud_api.crud_app.exception.ValidationErrorResponse;
import com.crud_api.crud_app.exception.ValidationErrorResponse.Violation;
import com.crud_api.crud_app.exception.NotFoundException;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Обработка ошибок валидации @Valid на @RequestBody
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        List<Violation> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                .toList();
        ValidationErrorResponse response = new ValidationErrorResponse("Validation failed", violations);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Обработка ошибок ConstraintViolation (например, при валидации @PathVariable, @RequestParam)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        List<Violation> violations = ex.getConstraintViolations().stream()
                .map(violation -> new Violation(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()
                ))
                .toList();
        ValidationErrorResponse response = new ValidationErrorResponse("Validation failed", violations);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Обработка ошибок JSON (невалидный формат, неизвестные поля, ошибки маппинга)
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
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
        logger.warn("JSON parse error: {}", friendlyMessage, ex);
        return ResponseEntity.badRequest().body(new ErrorResponse(friendlyMessage));
    }

    // Обработка ошибок типа NotFound (свои и JPA)
    @ExceptionHandler({EntityNotFoundException.class, NotFoundException.class, EmptyResultDataAccessException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(Exception ex) {
        logger.info("Not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Not found: " + ex.getMessage()));
    }

    // Ошибка нарушения ограничений БД
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        logger.warn("Data integrity violation", ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("Database error: referential integrity violation: non-existent reference to another table"));
    }

    // Ошибка несоответствия типа параметра
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        String msg = "Type mismatch: parameter '" + ex.getName() + "' should be " + requiredType;
        logger.warn(msg);
        return ResponseEntity.badRequest().body(new ErrorResponse(msg));
    }

    // Отсутствие обязательного параметра
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        String msg = "Missing request parameter: " + ex.getParameterName();
        logger.warn(msg);
        return ResponseEntity.badRequest().body(new ErrorResponse(msg));
    }

    // Метод не поддерживается
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        String msg = "HTTP method not allowed: " + ex.getMethod();
        logger.warn(msg);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(new ErrorResponse(msg));
    }

    // Обработка всех остальных исключений
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("Unhandled exception", ex);
        return new ResponseEntity<>(
                new ErrorResponse("Internal server error. Please contact support."),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
