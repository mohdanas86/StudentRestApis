package com.anas.StudentRestApis.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * UserEntity - Represents an authenticated user in the system
 *
 * Design Principles:
 * - Immutable after creation (userId, createdAt)
 * - Passwords never exposed in serialization (@JsonIgnore)
 * - Audit trail (createdAt, updatedAt)
 * - Optional teacher relationship (not all users are teachers)
 * - Active status for soft deletion
 */
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_email", columnList = "email"),
        @Index(name = "idx_users_username", columnList = "username"),
        @Index(name = "idx_users_role", columnList = "role"),
        @Index(name = "idx_users_is_active", columnList = "is_active")
})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    // ==================== Primary Key ====================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;

    // ==================== Authentication Credentials ====================
    @Column(unique = true, nullable = false, length = 255)
    private String email;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    /**
     * SECURITY CRITICAL:
     * - Always BCrypt hashed (60 chars when stored)
     * - Never expose in API responses
     * - Never log in plain text
     */
    @Column(name = "password_hash", nullable = false, length = 255)
    @JsonIgnore
    private String passwordHash;

    // ==================== Authorization ====================
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean isActive;

    // ==================== Relationships ====================
    /**
     * Optional relationship to TeacherEntity
     * - A user may not be a teacher (e.g., admin)
     * - A teacher must have a user account
     * - Nullable and unique to maintain 1-to-1 relationship
     */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teacher_id", unique = true, nullable = true, foreignKey = @ForeignKey(name = "fk_user_teacher"))
    private TeacherEntity teacher;

    // ==================== Audit Fields ====================
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // ==================== Business Methods ====================

    /**
     * Check if user has a specific role
     */
    public boolean hasRole(Role requiredRole) {
        return this.role == requiredRole;
    }

    /**
     * Check if user has any of the specified roles
     */
    public boolean hasAnyRole(Role... roles) {
        for (Role role : roles) {
            if (this.role == role) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if user can be deleted (only inactive users)
     */
    public boolean canBeDeleted() {
        return !this.isActive;
    }

    /**
     * Soft delete - mark as inactive instead of hard delete
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * Reactivate user account
     */
    public void activate() {
        this.isActive = true;
    }

}
