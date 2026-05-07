package com.jenwright.booking.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for REST API errors.
 *
 * Centrally handles exceptions thrown across the application and converts them
 * to appropriate HTTP responses with meaningful error messages and status codes.
 * This advice applies to all REST controllers in the application.
 *
 * @author jen
 * @version 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles IllegalArgumentException and returns BAD_REQUEST status.
     *
     * Catches validation errors and identifier/resource not found scenarios
     * and returns a 400 Bad Request response with the error message.
     *
     * @param ex the IllegalArgumentException thrown
     * @return ResponseEntity with BAD_REQUEST status and error message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Handles IllegalStateException and returns CONFLICT status.
     *
     * Catches business logic violations such as resource availability conflicts
     * and returns a 409 Conflict response with the error message.
     *
     * @param ex the IllegalStateException thrown
     * @return ResponseEntity with CONFLICT status and error message
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleConflict(IllegalStateException ex) {
        return error(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * Handles SecurityException and returns FORBIDDEN status.
     *
     * Catches authorization failures when a user attempts to perform an action
     * they do not have permission for, and returns a 403 Forbidden response.
     *
     * @param ex the SecurityException thrown
     * @return ResponseEntity with FORBIDDEN status and error message
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, String>> handleForbidden(SecurityException ex) {
        return error(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    /**
     * Handles validation errors from request body validation.
     *
     * Catches Spring validation exceptions when request body fields fail
     * validation constraints, and returns a 400 Bad Request with a map of
     * field names to validation error messages.
     *
     * @param ex the MethodArgumentNotValidException thrown by Spring
     * @return ResponseEntity with BAD_REQUEST status and field validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Constructs a standardized error response.
     *
     * Helper method that creates a consistent error response format with
     * the specified HTTP status and error message.
     *
     * @param status the HTTP status code to return
     * @param message the error message
     * @return ResponseEntity containing the error response with the specified status
     */
    private ResponseEntity<Map<String, String>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("error", message));
    }
}
