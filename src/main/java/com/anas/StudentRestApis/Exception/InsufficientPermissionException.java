package com.anas.StudentRestApis.Exception;

public class InsufficientPermissionException extends RuntimeException {
    public InsufficientPermissionException(String message) {
        super(message);
    }

    public InsufficientPermissionException(String message, Throwable cause) {
        super(message, cause);
    }
}
