package com.anas.StudentRestApis.Repository;

import com.anas.StudentRestApis.Entity.AuditAction;
import com.anas.StudentRestApis.Entity.AuditLogEntity;
import com.anas.StudentRestApis.Entity.AuditStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for audit log queries.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long> {

    /** Find logs by entity type and id. */
    List<AuditLogEntity> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
            String entityType, Long entityId);

    /** Find logs by user id. */
    List<AuditLogEntity> findByUserUserIdOrderByCreatedAtDesc(Long userId);

    /** Find logs by action. */
    List<AuditLogEntity> findByActionOrderByCreatedAtDesc(AuditAction action);

    /** Find logs by action and entity type. */
    List<AuditLogEntity> findByActionAndEntityTypeOrderByCreatedAtDesc(
            AuditAction action, String entityType);

    /** Find logs in date range. */
    List<AuditLogEntity> findByCreatedAtBetweenOrderByCreatedAtDesc(
            LocalDateTime startDate, LocalDateTime endDate);

    /** Find logs by status. */
    List<AuditLogEntity> findByStatusOrderByCreatedAtDesc(AuditStatus status);

    /** Find logs with specification and pagination. */
    Page<AuditLogEntity> findAll(Specification<AuditLogEntity> spec, Pageable pageable);

    /** Count logs by entity. */
    long countByEntityTypeAndEntityId(String entityType, Long entityId);

    /** Count logs by status. */
    long countByStatus(AuditStatus status);

    /** Find old logs for archive. */
    List<AuditLogEntity> findByCreatedAtBefore(LocalDateTime cutoffDate, Pageable pageable);
}