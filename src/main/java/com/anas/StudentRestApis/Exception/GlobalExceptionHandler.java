package com.anas.StudentRestApis.Exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler - Centralized exception handling
 *
 * @RestControllerAdvice: Applied to all @RestController classes
 *
 * Catches exceptions and converts to ErrorResponse
 * Returns appropriate HTTP status codes
 * Logs errors for monitoring
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handle ResourceNotFoundException (404)
     *
     * When: Resource not found in database
     * HTTP Status: 404 Not Found
     * Example: Student with id 999 not found
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            WebRequest request
    ){
        log.warn("Resource not found: {}", ex.getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .type("about:blank")
                .title("Not Found")
                .status(HttpStatus.NOT_FOUND.value())
                .details(ex.getMessage())
                .instance(request.getDescription(false).replace("uri=", ""))
                .timestamp(LocalDateTime.now())
                .code("RESOURCE_NOT_FOUND")
                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle ValidationException (400)
     *
     * When: Invalid input data or failed validation
     * HTTP Status: 400 Bad Request
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            ValidationException ex,
            WebRequest request
    ){
        log.warn("Validation failed: {}", ex.getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .type("about:blank")
                .title("Bad Request")
                .status(HttpStatus.BAD_REQUEST.value())
                .details(ex.getMessage())
                .instance(request.getDescription(false).replace("uri=", ""))
                .timestamp(LocalDateTime.now())
                .code("VALIDATION_ERROR")
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle DuplicateResourceException (409)
     *
     * When: Resource already exists (unique constraint)
     * HTTP Status: 409 Conflict
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(
            DuplicateResourceException ex,
            WebRequest request
    ){
        log.warn("Duplicate resource: {}", ex.getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .type("about:blank")
                .title("Conflict")
                .status(HttpStatus.CONFLICT.value()) // Corrected from CONTINUE
                .details(ex.getMessage())
                .instance(request.getDescription(false).replace("uri=", ""))
                .timestamp(LocalDateTime.now())
                .code("DUPLICATE_RESOURCE")
                .build();

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * Handle InvalidCredentialsException (401)
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentialException(
            InvalidCredentialsException ex,
            WebRequest request
    ) {
        log.warn("Invalid credential: {}", ex.getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .type("about:blank")
                .title("Unauthorized")
                .status(HttpStatus.UNAUTHORIZED.value())
                .details(ex.getMessage())
                .instance(request.getDescription(false).replace("uri=", ""))
                .timestamp(LocalDateTime.now())
                .code("INVALID_CREDENTIAL")
                .build();

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handle InsufficientPermissionException (403)
     */
    @ExceptionHandler(InsufficientPermissionException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientPermissionException(
            InsufficientPermissionException ex,
            WebRequest request
    ){
        log.warn("Permission denied: {}", ex.getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .type("about:blank")
                .title("Forbidden")
                .status(HttpStatus.FORBIDDEN.value())
                .details(ex.getMessage())
                .instance(request.getDescription(false).replace("uri=", ""))
                .timestamp(LocalDateTime.now())
                .code("INSUFFICIENT_PERMISSION")
                .build();

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /**
     * Handle MethodArgumentNotValidException (400)
     *
     * When: @Valid annotation validation fails
     * HTTP Status: 400 Bad Request
     * * NOTE: We override the parent method to avoid the "Ambiguous @ExceptionHandler" error.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        log.warn("Method argument validation failed");

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        ErrorResponse response = ErrorResponse.builder()
                .type("about:blank")
                .title("Bad Request")
                .status(status.value())
                .details("Validation failed")
                .instance(request.getDescription(false).replace("uri=", ""))
                .timestamp(LocalDateTime.now())
                .code("VALIDATION_ERROR")
                .errors(fieldErrors)
                .build();

        return new ResponseEntity<>(response, status);
    }

    /**
     * Handle Generic Exceptions (500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            WebRequest request) {

        log.error("Unexpected error occurred", ex);

        ErrorResponse response = ErrorResponse.builder()
                .type("about:blank")
                .title("Internal Server Error")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .details("An unexpected error occurred. Please try again later.")
                .instance(request.getDescription(false).replace("uri=", ""))
                .timestamp(LocalDateTime.now())
                .code("INTERNAL_ERROR")
                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}