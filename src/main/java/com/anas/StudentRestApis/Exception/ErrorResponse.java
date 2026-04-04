package com.anas.StudentRestApis.Exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * ErrorResponse - Standardized error response (RFC 7807)
 *
 * RFC 7807 Standard Fields:
 * - type: URI identifying problem type
 * - title: Short description
 * - status: HTTP status code
 * - detail: Long description
 * - instance: URI of instance
 *
 * Additional Fields:
 * - timestamp: When error occurred
 * - path: Request path
 * - errors: Detailed field errors (for validation)
 *
 * Example:
 * {
 *   "type": "about:blank",
 *   "title": "Not Found",
 *   "status": 404,
 *   "detail": "Student with id 123 not found",
 *   "instance": "/api/v1/students/123",
 *   "timestamp": "2026-04-02T10:30:00",
 *   "path": "/api/v1/students/123"
 * }
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // dont include null fields
public class ErrorResponse {

    // RFC 7807 Standard Fields
    private String type;
    private String title;
    private int status;
    private String details;
    private String instance;

    // additional Fields
    private LocalDateTime timestamp;
    private String path;
    private Map<String, String> errors;
    private String code;
}
