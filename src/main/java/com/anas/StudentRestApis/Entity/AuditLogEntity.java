package com.anas.StudentRestApis.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity for storing audit logs.
 */
@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_entity", columnList = "entity_type, entity_id"),
        @Index(name = "idx_audit_user", columnList = "user_id"),
        @Index(name = "idx_audit_action", columnList = "action"),
        @Index(name = "idx_audit_created", columnList = "created_at")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuditLogEntity {

    /** Audit log id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditId;

    /** User who performed the action. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    /** Entity type (TEACHER, COURSE, USER, etc.). */
    @Column(nullable = false, length = 50)
    private String entityType;

    /** Id of the target entity. */
    @Column(nullable = false)
    private Long entityId;

    /** Action type. */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuditAction action;

    /** JSON value before change. */
    @Column(columnDefinition = "TEXT")
    private String oldValues;

    /** JSON value after change. */
    @Column(columnDefinition = "TEXT")
    private String newValues;

    /** Client IP address. */
    @Column(length = 45)
    private String ipAddress;

    /** Request User-Agent header. */
    @Column(length = 500)
    private String userAgent;

    /** Operation status. */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuditStatus status;

    /** Error message for failed operations. */
    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    /** Request path. */
    @Column(name = "request_path", length = 500)
    private String requestPath;

    /** Request method. */
    @Column(name = "request_method", length = 10)
    private String requestMethod;

    /** Created timestamp. */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
