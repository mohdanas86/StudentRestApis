package com.anas.StudentRestApis.Services;

import com.anas.StudentRestApis.Dto.LoginRequestDto;
import com.anas.StudentRestApis.Dto.RegisterRequestDto;
import com.anas.StudentRestApis.Dto.UserEntityDto;
import com.anas.StudentRestApis.Entity.Role;
import com.anas.StudentRestApis.Exception.DuplicateResourceException;
import com.anas.StudentRestApis.Exception.InvalidCredentialsException;
import com.anas.StudentRestApis.Service.UserServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * UserServicesImplTest - Unit tests for UserServicesImpl
 * 
 * Tests user registration, login, password management, and role operations
 */
@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserServicesImplTest {
    @Autowired
    private UserServices userServices;

    private RegisterRequestDto validRegisterRequest;
    private LoginRequestDto validLoginRequest;
    private String testUsername;
    private String testPassword;

    @BeforeEach
    void setUp() {
        testUsername = "testUser";
        testPassword = "TestPassword123!";

        validRegisterRequest = RegisterRequestDto.builder()
                .email("test@example.com")
                .username(testUsername)
                .password(testPassword)
                .role(Role.TEACHER)
                .build();

        validLoginRequest = LoginRequestDto.builder()
                .username(testUsername)
                .password(testPassword)
                .build();
    }

    @Test
    void testRegisterNewUser_Success() {
        // when
        UserEntityDto result = userServices.register(validRegisterRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(testUsername);
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getRole()).isEqualTo(Role.TEACHER);
        assertThat(result.isActive()).isTrue();
    }

    @Test
    void testRegisterDuplicateEmail_ThrowsException() {
        // First registration succeeds
        userServices.register(validRegisterRequest);

        // Second registration with same email should fail
        RegisterRequestDto duplicateRequest = RegisterRequestDto.builder()
                .email("test@example.com") // Same email
                .username("differentUser")
                .password("DifferentPass123!")
                .role(Role.TEACHER)
                .build();

        assertThrows(DuplicateResourceException.class, () -> {
            userServices.register(duplicateRequest);
        });
    }

    @Test
    void testRegisterDuplicateUsername_ThrowsException() {
        // First registration succeeds
        userServices.register(validRegisterRequest);

        // Second registration with same username should fail
        RegisterRequestDto duplicateRequest = RegisterRequestDto.builder()
                .email("different@example.com")
                .username(testUsername) // Same username
                .password("DifferentPass123!")
                .role(Role.TEACHER)
                .build();

        assertThrows(DuplicateResourceException.class, () -> {
            userServices.register(duplicateRequest);
        });
    }

    @Test
    void testRegisterStudentRole_ThrowsException() {
        // Student role registration not allowed
        RegisterRequestDto studentRequest = RegisterRequestDto.builder()
                .email("student@example.com")
                .username("student")
                .password("StudentPass123!")
                .role(Role.STUDENT) // Not allowed
                .build();

        assertThrows(IllegalArgumentException.class, () -> {
            userServices.register(studentRequest);
        });
    }

    @Test
    void testLogin_Success() {
        // Register a user first
        userServices.register(validRegisterRequest);

        // Login with valid credentials
        var loginResponse = userServices.login(validLoginRequest);

        assertThat(loginResponse).isNotNull();
        assertThat(loginResponse.getUser().getUsername()).isEqualTo(testUsername);
        assertThat(loginResponse.getUser().getEmail()).isEqualTo("test@example.com");
        assertThat(loginResponse.getTokenType()).isEqualTo("Bearer");
        assertThat(loginResponse.getExpiresIn()).isGreaterThan(0);
    }

    @Test
    void testLogin_InvalidUsername_ThrowsException() {
        // Try login with non-existent user
        LoginRequestDto invalidRequest = LoginRequestDto.builder()
                .username("nonexistentUser")
                .password(testPassword)
                .build();

        assertThrows(InvalidCredentialsException.class, () -> {
            userServices.login(invalidRequest);
        });
    }

    @Test
    void testLogin_InvalidPassword_ThrowsException() {
        // Register a user first
        userServices.register(validRegisterRequest);

        // Try login with wrong password
        LoginRequestDto invalidRequest = LoginRequestDto.builder()
                .username(testUsername)
                .password("WrongPassword123!")
                .build();

        assertThrows(InvalidCredentialsException.class, () -> {
            userServices.login(invalidRequest);
        });
    }

    @Test
    void testValidatePassword_Success() {
        // Register a user
        userServices.register(validRegisterRequest);

        // Validate with correct password
        boolean isValid = userServices.validatePassword(testUsername, testPassword);

        assertTrue(isValid);
    }

    @Test
    void testValidatePassword_InvalidPassword() {
        // Register a user
        userServices.register(validRegisterRequest);

        // Validate with wrong password
        boolean isValid = userServices.validatePassword(testUsername, "WrongPassword123!");

        assertFalse(isValid);
    }

    @Test
    void testChangePassword_Success() {
        // Register a user
        userServices.register(validRegisterRequest);

        // Change password
        String newPassword = "NewPassword456!";
        userServices.changePassword(1L, testPassword, newPassword);

        // Verify new password works
        boolean isNewPasswordValid = userServices.validatePassword(testUsername, newPassword);
        assertTrue(isNewPasswordValid);

        // Verify old password doesn't work
        boolean isOldPasswordValid = userServices.validatePassword(testUsername, testPassword);
        assertFalse(isOldPasswordValid);
    }

    @Test
    void testChangePassword_InvalidOldPassword_ThrowsException() {
        // Register a user
        userServices.register(validRegisterRequest);

        // Try to change password with wrong old password
        assertThrows(InvalidCredentialsException.class, () -> {
            userServices.changePassword(1L, "WrongOldPassword123!", "NewPassword456!");
        });
    }

    @Test
    void testGetUserById_Success() {
        // Register a user
        UserEntityDto registered = userServices.register(validRegisterRequest);

        // Get user by ID
        UserEntityDto retrieved = userServices.getUserById(registered.getUserId());

        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getUserId()).isEqualTo(registered.getUserId());
        assertThat(retrieved.getUsername()).isEqualTo(testUsername);
    }

    @Test
    void testGetUserByUsername_Success() {
        // Register a user
        userServices.register(validRegisterRequest);

        // Get user by username
        UserEntityDto retrieved = userServices.getUserByUsername(testUsername);

        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getUsername()).isEqualTo(testUsername);
        assertThat(retrieved.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void testUpdateUserRole_Success() {
        // Register as TEACHER
        UserEntityDto registered = userServices.register(validRegisterRequest);

        // Update to ADMIN
        userServices.updateUserRole(registered.getUserId(), Role.ADMIN);

        // Verify role changed
        UserEntityDto updated = userServices.getUserById(registered.getUserId());
        assertThat(updated.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void testDeactivateUser_Success() {
        // Register a user
        UserEntityDto registered = userServices.register(validRegisterRequest);

        // Deactivate user
        userServices.deactivateUser(registered.getUserId());

        // Verify deactivated
        UserEntityDto deactivated = userServices.getUserById(registered.getUserId());
        assertFalse(deactivated.isActive());
    }

    @Test
    void testReactivateUser_Success() {
        // Register and deactivate
        UserEntityDto registered = userServices.register(validRegisterRequest);
        userServices.deactivateUser(registered.getUserId());

        // Reactivate user
        userServices.reactivateUser(registered.getUserId());

        // Verify reactivated
        UserEntityDto reactivated = userServices.getUserById(registered.getUserId());
        assertTrue(reactivated.isActive());
    }

    @Test
    void testIsUsernameAvailable_Success() {
        // Register a user
        userServices.register(validRegisterRequest);

        // Check availability
        boolean available = userServices.isUsernameAvailable("availableUser");
        assertTrue(available);

        boolean notAvailable = userServices.isUsernameAvailable(testUsername);
        assertFalse(notAvailable);
    }

    @Test
    void testIsEmailAvailable_Success() {
        // Register a user
        userServices.register(validRegisterRequest);

        // Check availability
        boolean available = userServices.isEmailAvailable("available@example.com");
        assertTrue(available);

        boolean notAvailable = userServices.isEmailAvailable("test@example.com");
        assertFalse(notAvailable);
    }
}
