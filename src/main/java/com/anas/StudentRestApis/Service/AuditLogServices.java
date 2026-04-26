package com.anas.StudentRestApis.Service;

import com.anas.StudentRestApis.Entity.AuditAction;
import com.anas.StudentRestApis.Entity.AuditLogEntity;
import com.anas.StudentRestApis.Entity.AuditStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Service contract for audit logging.
 */
public interface AuditLogServices {

        /** Log one audit action. */
        void logAction(Long userId, String entityType, Long entityId,
                        AuditAction action, Object oldValues, Object newValues,
                        HttpServletRequest request, AuditStatus status,
                        String errorMessage);

        /** Get paginated logs. */
        Page<AuditLogEntity> getAuditLogs(
                        String entityType, AuditAction action, Long userId,
                        LocalDateTime startDate, LocalDateTime endDate, int page, int size);

        /** Get logs for one entity. */
        List<AuditLogEntity> getEntityAuditHistory(String entityType, Long entityId);

        /** Get logs for one user. */
        List<AuditLogEntity> getUserAuditLogs(Long userId);

        /** Get overall statistics. */
        Map<String, Object> getAuditStatistics();

        /** Get statistics for a date range. */
        Map<String, Object> getAuditStatisticsForDateRange(
                        LocalDateTime startDate, LocalDateTime endDate);

        /** Count logs eligible for archive. */
        long archiveOldAuditLogs(LocalDateTime cutoffDate);

        /** Search logs by keyword. */
        List<AuditLogEntity> searchAuditLogs(String keyword, int limit);
}
