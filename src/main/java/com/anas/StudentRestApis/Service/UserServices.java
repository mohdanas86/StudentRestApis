package com.anas.StudentRestApis.Service;

import com.anas.StudentRestApis.Dto.*;
import com.anas.StudentRestApis.Entity.Role;
import com.anas.StudentRestApis.Entity.UserEntity;

import java.util.List;

/**
 * UserService - Business logic for user management and authentication
 *
 * This interface defines all user-related operations.
 * Implementation handles security, validation, and data persistence.
 */

public interface UserServices {
    // ==================== Authentication ====================

    /**
     * Register a new user (admin or teacher self-registration)
     *
     * @param request Contains email, username, password, role, optional teacherId
     * @return Created user (without password)
     * @throws IllegalArgumentException if email or username already exists
     * @throws IllegalArgumentException if role is STUDENT (use invitation system)
     */
   UserEntityDto register(RegisterRequestDto request);

    /**
     * Authenticate user and generate JWT token
     *
     * @param request Contains username and password
     * @return Login response with JWT token and user info
     * @throws com.anas.StudentRestApis.Exception.InvalidCredentialsException if username not found or password mismatch
     */
    LoginResponseDto login(LoginRequestDto request);

    /**
     * Validate user password (used for sensitive operations)
     *
     * @param username User's username
     * @param password Plain text password to verify
     * @return true if password matches, false otherwise
     */
    boolean validatePassword(String username, String password);

    /**
     * Change user password
     *
     * @param userId User to update
     * @param oldPassword Current password for verification
     * @param newPassword New password (will be hashed)
     * @throws com.anas.StudentRestApis.Exception.InvalidCredentialsException if old password doesn't match
     */
    void changePassword(Long userId, String oldPassword, String newPassword);


    // ==================== User Management ====================

    /**
     * Get user by ID
     *
     * @param userId User ID to fetch
     * @return User details
     * @throws com.anas.StudentRestApis.Exception.ResourceNotFoundException if user not found
     */
    UserEntityDto getUserById(Long userId);

    /**
     * Get user by username
     *
     * @param username Username to search
     * @return User details
     * @throws com.anas.StudentRestApis.Exception.ResourceNotFoundException if user not found
     */
    UserEntityDto getUserByUsername(String username);

    /**
     * Get all users (admin only)
     *
     * @param role Optional filter by role
     * @return List of all users (by role if specified)
     */
    List<UserEntityDto> getAllUsers(Role role);

    /**
     * Get all active users
     *
     * @return List of active users
     */
    List<UserEntityDto> getActiveUsers();

    /**
     * Update user information (non-password fields)
     *
     * @param userId User to update
     * @param request Update data
     * @return Updated user
     * @throws com.anas.StudentRestApis.Exception.ResourceNotFoundException if user not found
     */
    UserEntityDto updateUser(Long userId, UpdateUserRequestDto request);

    /**
     * Deactivate user account (soft delete)
     *
     * @param userId User to deactivate
     * @throws com.anas.StudentRestApis.Exception.ResourceNotFoundException if user not found
     */
    void deactivateUser(Long userId);

    /**
     * Reactivate user account
     *
     * @param userId User to reactivate
     * @throws com.anas.StudentRestApis.Exception.ResourceNotFoundException if user not found
     */
    void reactivateUser(Long userId);

    /**
     * Delete user permanently (hard delete - admin only)
     *
     * @param userId User to delete
     * @throws com.anas.StudentRestApis.Exception.ResourceNotFoundException if user not found
     */
    void deleteUser(Long userId);

    // ==================== Role Management ====================

    /**
     * Update user role (admin only)
     *
     * @param userId User to update
     * @param newRole New role to assign
     * @throws com.anas.StudentRestApis.Exception.ResourceNotFoundException if user not found
     */
    void updateUserRole(Long userId, Role newRole);

    /**
     * Get all users with specific role
     *
     * @param role Role to filter by
     * @return List of users with this role
     */
    List<UserEntityDto> getUserByRole(Role role);

    /**
     * Count users by role
     *
     * @param role Role to count
     * @return Number of users with this role
     */
    long countUserByRole(Role role);


    // ==================== Search & Verification ====================

    /**
     * Search users by username or email
     *
     * @param searchTerm Search query
     * @return Matching users
     */
    List<UserEntityDto> searchUsers(String searchTerm);

    /**
     * Check if username is available
     *
     * @param username Username to check
     * @return true if available, false if taken
     */
    boolean isUsernameAvailable(String username);

    /**
     * Check if email is available
     *
     * @param email Email to check
     * @return true if available, false if taken
     */
    boolean isEmailAvailable(String email);

    /**
     * Get the underlying UserEntity (internal use only)
     *
     * @param username Username to lookup
     * @return UserEntity object
     */
    UserEntity getUserEntityByUsername(String username);
}
