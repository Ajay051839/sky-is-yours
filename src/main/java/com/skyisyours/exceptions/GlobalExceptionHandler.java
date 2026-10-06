package com.skyisyours.exceptions;

import com.skyisyours.payload.APIResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Centralized advice intercepting application-wide exceptions and formatting standard HTTP error responses
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handles validation failures on @RequestBody DTO models (maps rejected fields to constraint messages)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> NewMethodArgumentNotValidException(MethodArgumentNotValidException e)
    {
        Map <String, String> response = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(
                err ->
                {
                    String fieldName = ((FieldError)err).getField();
                    String message = err.getDefaultMessage();
                    response.put(fieldName, message);
                }
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handles missing entities by converting domain ResourceNotFoundException to standard 404 response
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse> newResourceNotFoundException(ResourceNotFoundException e)
    {
        APIResponse response = new APIResponse(e.getMessage(), false);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Handles generic custom business rule violations as 400 Bad Request
    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIResponse> newAPIException(APIException e)
    {
        APIResponse response = new APIResponse(e.getMessage(), false);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handles Jakarta entity-level constraint violations before persistence
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<APIResponse> newConstraintViolationException()
    {
        String message = "The request contains a value which is already present in the Database";
        APIResponse response = new APIResponse(message, false);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    // Handles database constraint errors (e.g. duplicate keys) and parses SQL messages into user-friendly responses
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<APIResponse> newDataIntegrityViolationException(DataIntegrityViolationException e)
    {
        String message = extractUniqueConstraintMessage(e.getMessage());
        APIResponse response = new APIResponse(message, false);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    // Handles Spring 6+ validation failures on method arguments like @RequestParam and @PathVariable
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String, String>> handleHandlerMethodValidationException(HandlerMethodValidationException e) {
        Map<String, String> response = new HashMap<>();

        e.getAllErrors().forEach(error -> {
            String paramName = "parameter";
            if (error.getCodes() != null && error.getCodes().length > 0) {
                String[] parts = error.getCodes()[0].split("\\.");
                paramName = parts[parts.length - 1];
            }
            response.put(paramName, error.getDefaultMessage());
        });
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Parses raw H2 and MySQL duplicate entry error strings using regex to mask SQL internals
    private String extractUniqueConstraintMessage(String errorMsg) {
        if (errorMsg == null) {
            return "A record with this value already exists.";
        }

        // Pattern for H2: ... ON PUBLIC.AIRPORT(AIRPORT_CODE ...) VALUES ( ... 'BLR' ) ...
        Pattern h2Pattern = Pattern.compile("\\((.*?)\\s+NULLS.*?VALUES\\s*\\(.*?\\'(.*?)\\'\\s*\\)", Pattern.CASE_INSENSITIVE);
        Matcher h2Matcher = h2Pattern.matcher(errorMsg);
        if (h2Matcher.find()) {
            String column = h2Matcher.group(1).trim().toLowerCase();
            String value = h2Matcher.group(2).trim();
            return String.format("A record with %s '%s' already exists.", column, value);
        }

        // Pattern for MySQL: Duplicate entry 'BLR' for key '...'
        Pattern mysqlPattern = Pattern.compile("Duplicate entry '(.*?)' for key '(.*?)'", Pattern.CASE_INSENSITIVE);
        Matcher mysqlMatcher = mysqlPattern.matcher(errorMsg);
        if (mysqlMatcher.find()) {
            String value = mysqlMatcher.group(1);
            String key = mysqlMatcher.group(2);
            // Extracts the column or key name if prefixed by table name (e.g. airport.UK_airport_code)
            String field = key.contains(".") ? key.substring(key.lastIndexOf('.') + 1) : key;
            return String.format("A record with value '%s' already exists for %s.", value, field);
        }

        // Fallback if regex doesn't match
        return "The request contains a duplicate value that violates a unique constraint.";
    }
}