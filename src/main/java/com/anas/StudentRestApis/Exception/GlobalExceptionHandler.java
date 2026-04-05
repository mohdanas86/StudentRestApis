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
 * GlobalExceptionHandler - Centralized exception handling for REST API
 * Catches exceptions and converts to standardized ErrorResponse
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

        private ErrorResponse buildErrorResponse(Exception ex, HttpStatus status, String code, WebRequest request) {
                return ErrorResponse.builder()
                                .type("about:blank")
                                .title(status.getReasonPhrase())
                                .status(status.value())
                                .details(ex.getMessage())
                                .instance(request.getDescription(false).replace("uri=", ""))
                                .timestamp(LocalDateTime.now())
                                .code(code)
                                .build();
        }

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
                        ResourceNotFoundException ex, WebRequest request) {
                log.warn("Resource not found: {}", ex.getMessage());
                return new ResponseEntity<>(
                                buildErrorResponse(ex, HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", request),
                                HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(ValidationException.class)
        public ResponseEntity<ErrorResponse> handleValidationException(
                        ValidationException ex, WebRequest request) {
                log.warn("Validation failed: {}", ex.getMessage());
                return new ResponseEntity<>(
                                buildErrorResponse(ex, HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", request),
                                HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(DuplicateResourceException.class)
        public ResponseEntity<ErrorResponse> handleDuplicateResourceException(
                        DuplicateResourceException ex, WebRequest request) {
                log.warn("Duplicate resource: {}", ex.getMessage());
                return new ResponseEntity<>(
                                buildErrorResponse(ex, HttpStatus.CONFLICT, "DUPLICATE_RESOURCE", request),
                                HttpStatus.CONFLICT);
        }

        @ExceptionHandler(InvalidCredentialsException.class)
        public ResponseEntity<ErrorResponse> handleInvalidCredentialException(
                        InvalidCredentialsException ex, WebRequest request) {
                log.warn("Invalid credential: {}", ex.getMessage());
                return new ResponseEntity<>(
                                buildErrorResponse(ex, HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIAL", request),
                                HttpStatus.UNAUTHORIZED);
        }

        @ExceptionHandler(InsufficientPermissionException.class)
        public ResponseEntity<ErrorResponse> handleInsufficientPermissionException(
                        InsufficientPermissionException ex, WebRequest request) {
                log.warn("Permission denied: {}", ex.getMessage());
                return new ResponseEntity<>(
                                buildErrorResponse(ex, HttpStatus.FORBIDDEN, "INSUFFICIENT_PERMISSION", request),
                                HttpStatus.FORBIDDEN);
        }

        @Override
        protected ResponseEntity<Object> handleMethodArgumentNotValid(
                        MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status,
                        WebRequest request) {
                log.warn("Method argument validation failed");

                Map<String, String> fieldErrors = new HashMap<>();
                ex.getBindingResult().getAllErrors().forEach(error -> {
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

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
                log.error("Unexpected error occurred", ex);
                return new ResponseEntity<>(
                                buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", request),
                                HttpStatus.INTERNAL_SERVER_ERROR);
        }
}