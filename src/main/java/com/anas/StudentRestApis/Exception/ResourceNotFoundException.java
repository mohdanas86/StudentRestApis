package com.anas.StudentRestApis.Exception;

/**
 * ResourceNotFoundException - For 404 Not Found errors
 *
 * Thrown when:
 * - Student not found
 * - User not found
 * - Order not found
 */
public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message){
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause){
        super(message, cause);
    }
}
