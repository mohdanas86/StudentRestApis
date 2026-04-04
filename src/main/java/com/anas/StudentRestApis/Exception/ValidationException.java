package com.anas.StudentRestApis.Exception;

/**
 * ValidationException - For 400 Bad Request errors
 *
 * Thrown when:
 * - Required field missing
 * - Invalid data format
 * - Business logic validation fails
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}