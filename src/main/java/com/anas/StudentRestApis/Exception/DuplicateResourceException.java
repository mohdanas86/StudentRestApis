package com.anas.StudentRestApis.Exception;
/**
 * DuplicateResourceException - For 409 Conflict errors
 *
 * Thrown when:
 * - Email already registered
 * - Username already taken
 * - Unique constraint violated
 */
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message){
        super(message);
    }

    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
