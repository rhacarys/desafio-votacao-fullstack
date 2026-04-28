package com.challenge.voting.infra.config;

import com.challenge.voting.domain.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the voting API.
 * Centralizes exception handling across all REST controllers to provide consistent error responses.
 * Handles business exceptions, validation errors, and generic exceptions.
 * 
 * <p>Handled exception types:
 * <ul>
 *   <li>BusinessException: Application business rule violations</li>
 *   <li>MethodArgumentNotValidException: Request validation failures</li>
 *   <li>Exception: Unexpected runtime errors</li>
 * </ul>
 * 
 * @author Voting Challenge API
 * @version 1.0.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Handles BusinessException thrown by services.
     * Maps specific error codes to appropriate HTTP status codes:
     * - CPF_INVALID maps to 404 NOT_FOUND
     * - Other business errors map to 422 UNPROCESSABLE_CONTENT
     * 
     * @param ex the BusinessException thrown
     * @return ResponseEntity with error details and appropriate HTTP status
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessException(BusinessException ex) {
        if ("CPF_INVALID".equals(ex.getCode())) {
            return buildResponse(HttpStatus.NOT_FOUND, "CPF_INVALID", ex.getMessage());
        }

        return buildResponse(HttpStatus.UNPROCESSABLE_CONTENT, ex.getCode(), ex.getMessage());
    }

    /**
     * Handles validation errors from request body validation.
     * Extracts the first field error and returns a descriptive error message.
     * 
     * @param ex the MethodArgumentNotValidException thrown
     * @return ResponseEntity with validation error details and 400 BAD_REQUEST status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        return buildResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", errorMessage);
    }

    /**
     * Handles unexpected runtime exceptions.
     * Logs a generic error message to prevent information leakage.
     * 
     * @param ex the unexpected exception thrown
     * @return ResponseEntity with generic error message and 500 INTERNAL_SERVER_ERROR status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "UNKNOWN_ERROR", "An unexpected error occurred.");
    }

    /**
     * Builds a standardized error response with timestamp, status, error code, and message.
     * 
     * @param status the HTTP status code
     * @param errorCode the application error code
     * @param message the error message
     * @return ResponseEntity with formatted error response body
     */
    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String errorCode, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("code", errorCode);
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}