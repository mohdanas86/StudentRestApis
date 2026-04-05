package com.anas.StudentRestApis.Exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * ErrorResponse - Standardized error format (RFC 7807)
 * Includes type, title, status, details, instance, timestamp, and field-level
 * errors
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private String type;
    private String title;
    private int status;
    private String details;
    private String instance;
    private LocalDateTime timestamp;
    private String path;
    private Map<String, String> errors;
    private String code;
}
