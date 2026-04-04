package com.anas.StudentRestApis.Exception;

/**
 * InsufficientPermissionException - For 403 Forbidden errors
 *
 * Thrown when:
 * - User lacks required role
 * - User lacks permission for resource
 * - Unauthorized operation
 */
public class InsufficientPermissionException extends RuntimeException {
    public InsufficientPermissionException(String message){
        super(message);
    }

    public InsufficientPermissionException(String message, Throwable cause){
        super(message, cause);
    }
}
