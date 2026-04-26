package com.anas.StudentRestApis.Service.Impl;

import com.anas.StudentRestApis.Entity.AuditAction;
import com.anas.StudentRestApis.Entity.AuditLogEntity;
import com.anas.StudentRestApis.Entity.AuditStatus;
import com.anas.StudentRestApis.Entity.UserEntity;
import com.anas.StudentRestApis.Repository.AuditLogRepository;
import com.anas.StudentRestApis.Repository.UserRepository;
import com.anas.StudentRestApis.Service.AuditLogServices;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service implementation for audit log operations.
 */
@Slf4j
@Transactional
@Service
@AllArgsConstructor
public class AuditLogServicesImpl implements AuditLogServices {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    /**
     * Save one audit record.
     */
    @Override
    public void logAction(Long userId, String entityType, Long entityId,
            AuditAction action, Object oldValues, Object newValues,
            HttpServletRequest request, AuditStatus status,
            String errorMessage) {
        try {
            log.debug("Logging audit action: entityType={}, entityId={}, action={}, status={}",
                    entityType, entityId, action, status);

            AuditLogEntity auditLog = new AuditLogEntity();

            // Set user when available.
            if (userId != null) {
                UserEntity user = userRepository.findById(userId).orElse(null);
                auditLog.setUser(user);
                log.debug("Audit user associated: userId={}", userId);
            }

            auditLog.setEntityType(entityType);
            auditLog.setEntityId(entityId);
            auditLog.setAction(action);
            auditLog.setStatus(status);
            auditLog.setErrorMessage(errorMessage);

            // Store before/after values as JSON.
            if (oldValues != null) {
                auditLog.setOldValues(objectMapper.writeValueAsString(oldValues));
                log.debug("Old values captured for audit: {}...",
                        auditLog.getOldValues().substring(0, Math.min(100, auditLog.getOldValues().length())));
            }

            if (newValues != null) {
                auditLog.setNewValues(objectMapper.writeValueAsString(newValues));
                log.debug("New values captured for audit: {}...",
                        auditLog.getNewValues().substring(0, Math.min(100, auditLog.getNewValues().length())));
            }

            // Store request details.
            if (request != null) {
                auditLog.setIpAddress(getClientIpAddress(request));
                auditLog.setUserAgent(request.getHeader("User-Agent"));
                auditLog.setRequestPath(request.getRequestURI());
                auditLog.setRequestMethod(request.getMethod());
                log.debug("Request info captured: ip={}, method={}, path={}",
                        auditLog.getIpAddress(), request.getMethod(), request.getRequestURI());
            }

            auditLogRepository.save(auditLog);
            log.info("Audit log successfully persisted: auditId recorded for action {} on {} with ID {}",
                    action, entityType, entityId);

        } catch (Exception e) {
            log.error("Failed to log audit action: entityType={}, entityId={}, action={}",
                    entityType, entityId, action, e);
        }
    }

    /**
     * Get paginated audit logs.
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Page<AuditLogEntity> getAuditLogs(
            String entityType, AuditAction action, Long userId,
            LocalDateTime startDate, LocalDateTime endDate, int page, int size) {

        log.debug("Querying audit logs: entityType={}, action={}, userId={}, dateRange=[{}, {}], page={}, size={}",
                entityType, action, userId, startDate, endDate, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Specification<AuditLogEntity> spec = (root, query, cb) -> cb.conjunction();

        if (entityType != null && !entityType.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("entityType"), entityType));
            log.debug("Added entityType filter: {}", entityType);
        }

        if (action != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("action"), action));
            log.debug("Added action filter: {}", action);
        }

        if (userId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("user").get("userId"), userId));
            log.debug("Added userId filter: {}", userId);
        }

        if (startDate != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(
                    root.get("createdAt"), startDate));
            log.debug("Added startDate filter: {}", startDate);
        }

        if (endDate != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(
                    root.get("createdAt"), endDate));
            log.debug("Added endDate filter: {}", endDate);
        }

        Page<AuditLogEntity> results = auditLogRepository.findAll(spec, pageable);
        log.info("Audit logs query completed: found {} total records", results.getTotalElements());

        return results;
    }

    /**
     * Get audit history for one entity.
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<AuditLogEntity> getEntityAuditHistory(String entityType, Long entityId) {
        log.info("Retrieving audit history for: entityType={}, entityId={}", entityType, entityId);

        List<AuditLogEntity> history = auditLogRepository
                .findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId);

        log.info("Retrieved {} audit records for entity {}/{}", history.size(), entityType, entityId);
        return history;
    }

    /**
     * Get audit logs for one user.
     */
    @Override
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#userId)")
    public List<AuditLogEntity> getUserAuditLogs(Long userId) {
        log.info("Retrieving audit logs for user: userId={}", userId);

        List<AuditLogEntity> logs = auditLogRepository.findByUserUserIdOrderByCreatedAtDesc(userId);

        log.info("Retrieved {} audit records for user {}", logs.size(), userId);
        return logs;
    }

    /**
     * Get overall audit statistics.
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> getAuditStatistics() {
        log.debug("Calculating comprehensive audit statistics");

        Map<String, Object> stats = new HashMap<>();

        long totalLogs = auditLogRepository.count();
        stats.put("totalAuditLogs", totalLogs);
        log.debug("Total audit logs: {}", totalLogs);

        long successCount = auditLogRepository.countByStatus(AuditStatus.SUCCESS);
        long failedCount = auditLogRepository.countByStatus(AuditStatus.FAILED);
        stats.put("successfulOperations", successCount);
        stats.put("failedOperations", failedCount);
        log.debug("Operation outcomes: {} successful, {} failed", successCount, failedCount);

        Map<String, Long> byAction = new HashMap<>();
        for (AuditAction action : AuditAction.values()) {
            long count = auditLogRepository.findByActionOrderByCreatedAtDesc(action).size();
            if (count > 0) {
                byAction.put(action.name(), count);
                log.debug("Action {} count: {}", action.name(), count);
            }
        }
        stats.put("operationsByAction", byAction);

        Map<String, Long> byEntity = new HashMap<>();
        List<String> entityTypes = Arrays.asList("TEACHER", "COURSE", "COLLEGE", "USER");
        for (String entityType : entityTypes) {
            long count = auditLogRepository.findByActionAndEntityTypeOrderByCreatedAtDesc(
                    null, entityType).size();
            if (count > 0) {
                byEntity.put(entityType, count);
                log.debug("Entity type {} count: {}", entityType, count);
            }
        }
        stats.put("operationsByEntity", byEntity);

        log.info("Audit statistics calculation completed");
        return stats;
    }

    /**
     * Get audit statistics for a date range.
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> getAuditStatisticsForDateRange(
            LocalDateTime startDate, LocalDateTime endDate) {

        log.info("Calculating audit statistics for date range: {} to {}", startDate, endDate);

        List<AuditLogEntity> logs = auditLogRepository
                .findByCreatedAtBetweenOrderByCreatedAtDesc(startDate, endDate);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalLogs", logs.size());

        long successCount = logs.stream()
                .filter(l -> l.getStatus() == AuditStatus.SUCCESS)
                .count();
        long failedCount = logs.stream()
                .filter(l -> l.getStatus() == AuditStatus.FAILED)
                .count();

        stats.put("successCount", successCount);
        stats.put("failedCount", failedCount);
        stats.put("dateRange", Map.of(
                "start", startDate.toString(),
                "end", endDate.toString()));

        log.info("Period statistics: {} total, {} successful, {} failed",
                logs.size(), successCount, failedCount);

        return stats;
    }

    /**
     * Count logs eligible for archive.
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public long archiveOldAuditLogs(LocalDateTime cutoffDate) {
        log.info("Archiving audit logs created before: {}", cutoffDate);

        Pageable batch = PageRequest.of(0, 1000);

        Page<AuditLogEntity> oldLogs = auditLogRepository
                .findAll((root, query, cb) -> cb.lessThan(root.get("createdAt"), cutoffDate), batch);

        long archived = oldLogs.getTotalElements();
        log.info("Archiving {} audit logs created before {}", archived, cutoffDate);

        // For now, only return count.
        return archived;
    }

    /**
     * Search audit logs by error message.
     */
    @Override
    public List<AuditLogEntity> searchAuditLogs(String keyword, int limit) {
        log.debug("Searching audit logs for keyword: {}", keyword);

        List<AuditLogEntity> results = auditLogRepository.findAll()
                .stream()
                .filter(log -> log.getErrorMessage() != null &&
                        log.getErrorMessage().contains(keyword))
                .limit(limit)
                .collect(Collectors.toList());

        log.info("Search completed: found {} matching audit logs", results.size());
        return results;
    }

    /**
     * Resolve client IP from headers.
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwarded = request.getHeader("X-Forwarded-For");
        if (xForwarded != null && !xForwarded.isEmpty()) {
            return xForwarded.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
