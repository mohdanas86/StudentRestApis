package com.anas.StudentRestApis.Exception;
/**
 * InvalidCredentialsException - For 401 Unauthorized errors
 *
 * Thrown when:
 * - Wrong password
 * - Invalid token
 * - User disabled
 */
public class InvalidCredentialsException extends RuntimeException {
   public InvalidCredentialsException(String message){
       super(message);
   }

   public InvalidCredentialsException(String message, Throwable cause){
       super(message, cause);
   }
}
