package com.anas.StudentRestApis.Controllers;

import com.anas.StudentRestApis.Common.ApiResponse;
import com.anas.StudentRestApis.Entity.AuditAction;
import com.anas.StudentRestApis.Entity.AuditLogEntity;
import com.anas.StudentRestApis.Entity.AuditStatus;
import com.anas.StudentRestApis.Service.AuditLogServices;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Audit log APIs for admin users.
 */
@Slf4j
@RestController
@RequestMapping("/audit-logs")
@PreAuthorize("hasRole('ADMIN')")
@AllArgsConstructor
public class AuditLogControllers {

    private final AuditLogServices auditLogServices;

    /**
     * Get audit logs with optional filters.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<AuditLogEntity>>> getAuditLogs(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) AuditAction action,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("Retrieving audit logs: entityType={}, action={}, userId={}, page={}, size={}",
                    entityType, action, userId, page, size);

            Page<AuditLogEntity> logs = auditLogServices.getAuditLogs(
                    entityType, action, userId, startDate, endDate, page, size);

            log.info("Successfully retrieved audit logs: {} total records, page {}/{}",
                    logs.getTotalElements(), page, logs.getTotalPages());

            return ResponseEntity.ok(ApiResponse.ok(logs, "Audit logs retrieved successfully"));

        } catch (IllegalArgumentException e) {
            log.warn("Invalid filter parameters provided: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid filter parameters", e.getMessage()));

        } catch (Exception e) {
            log.error("Error retrieving audit logs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve audit logs", e.getMessage()));
        }
    }

    /**
     * Get audit history for one entity.
     */
    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<ApiResponse<List<AuditLogEntity>>> getEntityHistory(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        try {
            log.info("Retrieving entity audit history: entityType={}, entityId={}", entityType, entityId);

            if (entityType == null || entityType.isEmpty()) {
                log.warn("Invalid entityType provided: null or empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Invalid entity type", "Entity type cannot be empty"));
            }

            if (entityId == null || entityId <= 0) {
                log.warn("Invalid entityId provided: {}", entityId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Invalid entity ID", "Entity ID must be positive"));
            }

            List<AuditLogEntity> history = auditLogServices.getEntityAuditHistory(entityType, entityId);

            log.info("Retrieved entity audit history: {} records found for {}/{}",
                    history.size(), entityType, entityId);

            if (history.isEmpty()) {
                log.debug("No audit history found for entity {}/{}", entityType, entityId);
                return ResponseEntity.ok(ApiResponse.ok(history, "No audit history found for entity"));
            }

            return ResponseEntity.ok(ApiResponse.ok(history, "Entity audit history retrieved successfully"));

        } catch (Exception e) {
            log.error("Error retrieving entity audit history", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve entity audit history", e.getMessage()));
        }
    }

    /**
     * Get audit logs for one user.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<AuditLogEntity>>> getUserAuditLogs(
            @PathVariable Long userId) {
        try {
            log.info("Retrieving user audit logs: userId={}", userId);

            if (userId == null || userId <= 0) {
                log.warn("Invalid userId provided: {}", userId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Invalid user ID", "User ID must be positive"));
            }

            List<AuditLogEntity> logs = auditLogServices.getUserAuditLogs(userId);

            log.info("Retrieved user audit logs: {} records found for userId={}", logs.size(), userId);

            if (logs.isEmpty()) {
                log.debug("No audit activity found for user {}", userId);
                return ResponseEntity.ok(ApiResponse.ok(logs, "No audit activity found for user"));
            }

            return ResponseEntity.ok(ApiResponse.ok(logs, "User audit logs retrieved successfully"));

        } catch (Exception e) {
            log.error("Error retrieving user audit logs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve user audit logs", e.getMessage()));
        }
    }

    /**
     * Get audit statistics.
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
                log.warn("Invalid date range: start date after end date");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Invalid date range", "Start date must be before end date"));
            }

            log.info("Retrieving audit statistics: startDate={}, endDate={}", startDate, endDate);

            Map<String, Object> stats;
            if (startDate != null && endDate != null) {
                stats = auditLogServices.getAuditStatisticsForDateRange(startDate, endDate);
            } else {
                stats = auditLogServices.getAuditStatistics();
            }

            log.info("Audit statistics calculated successfully");

            return ResponseEntity.ok(ApiResponse.ok(stats, "Audit statistics retrieved successfully"));

        } catch (IllegalArgumentException e) {
            log.warn("Invalid date range provided: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid date range", e.getMessage()));

        } catch (Exception e) {
            log.error("Error retrieving audit statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve audit statistics", e.getMessage()));
        }
    }
}
