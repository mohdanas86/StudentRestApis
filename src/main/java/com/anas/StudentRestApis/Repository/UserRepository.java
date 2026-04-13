package com.anas.StudentRestApis.Repository;

import com.anas.StudentRestApis.Entity.Role;
import com.anas.StudentRestApis.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * UserRepository - Data access layer for user management
 *
 * Design: Query methods follow naming conventions for auto-generation
 * Custom queries use @Query for complex operations
 */

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    // ==================== Basic Queries ====================

    /**
     * Find user by username (case-sensitive match)
     * Used during login authentication
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     * Find user by email (case-insensitive email lookup)
     */
    @Query("SELECT u FROM UserEntity u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<UserEntity> findByEmailIgnoreCase(@Param("email") String email);

    /**
     * Check if username already exists
     */
    boolean existsByUsernameIgnoreCase(String username);

    /**
     * Check if email already exists
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UserEntity u WHERE LOWER(u.email) = LOWER(:email)")
    boolean existsByEmailIgnoreCase(@Param("email") String email);

    // ==================== Role-Based Queries ====================

    /**
     * Find all users with specific role
     */
    List<UserEntity> findByRole(Role role);

    /**
     * Find all active users with specific role
     */
    List<UserEntity> findByRoleAndIsActiveTrue(Role role);

    /**
     * Count users by role
     */
    long countByRole(Role role);

    /**
     * Count active users by role
     */
    long countByRoleAndIsActiveTrue(Role role);

    // ==================== Active Status Queries ====================

    /**
     * Find all active users
     */
    List<UserEntity> findByIsActiveTrue();

    /**
     * Find all inactive users
     */
    List<UserEntity> findByIsActiveFalse();

    /**
     * Find all active teachers (users with TEACHER role who are active)
     * Uses JPQL with enum comparison - Hibernate handles enum string conversion
     */
    @Query("SELECT u FROM UserEntity u WHERE u.role = com.anas.StudentRestApis.Entity.Role.TEACHER AND u.isActive = true")
    List<UserEntity> findActiveTeachers();

    // ==================== Teacher Relationship Queries ====================

    /**
     * Find user by associated teacher ID
     * Uses nested path: teacher_teacherId to traverse the OneToOne relationship
     */
    Optional<UserEntity> findByTeacher_TeacherId(Long teacherId);

    /**
     * Find all users who are linked to teachers
     */
    @Query("SELECT u FROM UserEntity u WHERE u.teacher IS NOT NULL")
    List<UserEntity> findAllTeacherUsers();

    // ==================== Search Queries ====================

    /**
     * Search users by username or email (case-insensitive)
     */
    @Query("""
            SELECT u FROM UserEntity u
            WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
               OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
            ORDER BY u.createdAt DESC
            """)
    List<UserEntity> searchByUsernameOrEmail(@Param("searchTerm") String searchTerm);
}
