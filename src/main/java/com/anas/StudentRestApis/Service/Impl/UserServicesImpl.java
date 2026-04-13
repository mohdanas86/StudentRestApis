package com.anas.StudentRestApis.Service.Impl;

import com.anas.StudentRestApis.Dto.*;
import com.anas.StudentRestApis.Entity.Role;
import com.anas.StudentRestApis.Entity.TeacherEntity;
import com.anas.StudentRestApis.Entity.UserEntity;
import com.anas.StudentRestApis.Exception.DuplicateResourceException;
import com.anas.StudentRestApis.Exception.InvalidCredentialsException;
import com.anas.StudentRestApis.Exception.ResourceNotFoundException;
import com.anas.StudentRestApis.Repository.TeacherRepository;
import com.anas.StudentRestApis.Repository.UserRepository;
import com.anas.StudentRestApis.Service.UserServices;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * UserServiceImpl - Implementation of user management business logic
 *
 * Key Responsibilities:
 * 1. User registration with validation
 * 2. Password hashing and verification
 * 3. User lookup and management
 * 4. Role-based operations
 * 5. Account activation/deactivation
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserServicesImpl implements UserServices {

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;


    // ==================== Authentication ====================
    // register user
    @Override
    @Transactional
    public UserEntityDto register(RegisterRequestDto request){
        log.info("Registering new user: {}", request.getUsername());

        if(userRepository.existsByEmailIgnoreCase(request.getEmail())){
            log.warn("Registration failed: Email already exists: {}", request.getEmail());
            throw new DuplicateResourceException("Email already in use: " + request.getEmail());
        }

        // Validate username not already in use
        if(userRepository.existsByUsernameIgnoreCase(request.getUsername())){
            log.warn("Registration failed: Username already exists: {}", request.getUsername());
            throw new DuplicateResourceException("Username already in use: " + request.getUsername());
        }

        // Prevent direct student registration (must use invitation system)
        if(request.getRole() == Role.STUDENT){
            log.info("Registration failed: Direct STUDENT registration not allowed");
            throw new IllegalArgumentException("Student must be registered by administrators");
        }

        // Link teacher if teacherId provided
        TeacherEntity teacher = null;
        if (request.getTeacherId() != null) {
            teacher = teacherRepository.findById(request.getTeacherId())
                    .orElseThrow(() -> {
                        log.warn("Registration failed: Teacher not found: {}", request.getTeacherId());
                        return new ResourceNotFoundException("Teacher not found with id: " + request.getTeacherId());
                    });
        }

        // Create new user
        UserEntity user = UserEntity.builder()
                .role(request.getRole())
                .teacher(teacher)
                .isActive(true)
                .build();

        UserEntity savedUser = userRepository.save(user);
        log.info("User registered successfully: {} (ID: {})", savedUser.getUsername(), savedUser.getUserId());
        return modelMapper.map(savedUser, UserEntityDto.class);
    }

    // login user
    @Override
    @Transactional
    public LoginResponseDto login(LoginRequestDto request){
        log.info("Login attempt for username: {}", request.getUsername());

        // Find user by username
        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(()-> {
                    log.warn("Login failed: Username not found: {}", request.getUsername());
                    // Generic message to prevent username enumeration
                    throw new InvalidCredentialsException("Invalid username or password");
                });

        // check if user is active
        if(!user.isActive()){
            log.warn("Login failed: User inactive: {}", request.getUsername());
            throw new InvalidCredentialsException("User account is deactivated");
        }

        // verify password
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            log.warn("Login failed: Invalid password for username: {}", request.getUsername());
            throw new InvalidCredentialsException("Invalid username or password");
        }

        log.info("Login successful for user: {} (ID: {})", user.getUsername(), user.getUserId());

        // JWT token generation will be handled by AuthenticationController
        // Return auth token details
        return LoginResponseDto.builder()
                .tokenType("Bearer")
                .expiresIn(86400) // 24 hours
                .user(
                        LoginResponseDto.UserInfoDto.builder()
                                .userId(user.getUserId())
                                .username(user.getUsername())
                                .email(user.getEmail())
                                .role(user.getRole())
                                .createdAt(user.getCreatedAt())
                                .build()
                )
                .build();
    }

    // validate password
    @Override
    @Transactional(readOnly = true)
    public boolean validatePassword(String username, String plainPassword) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        return passwordEncoder.matches(plainPassword, user.getPassword());
    }

    // change password
    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword){
        log.info("Password change requested for user ID: {}", userId);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(()->new ResourceNotFoundException("User not found with id: " + userId));

        // verify old password
        if(!passwordEncoder.matches(oldPassword, user.getPassword())){
            log.warn("Password change failed: Invalid old password for user ID: {}", userId);
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        // update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password changed successfully for user ID: {}", userId);
    }

    // ==================== User Management ====================
    // get user by userId
    @Override
    @Transactional(readOnly = true)
    public UserEntityDto getUserById(Long userId){
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return modelMapper.map(user, UserEntityDto.class);
    }

    // get user by username
    @Override
    @Transactional(readOnly = true)
    public UserEntityDto getUserByUsername(String username){
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        return modelMapper.map(user, UserEntityDto.class);
    }

    // get all users
    @Override
    @Transactional(readOnly = true)
    public List<UserEntityDto> getAllUsers(Role role){
        List<UserEntity> users = role == null
                ? userRepository.findAll()
                : userRepository.findByRole(role);

        return users.stream()
                .map(user -> modelMapper.map(user, UserEntityDto.class))
                .toList();
    }

    // get all active users
    @Override
    @Transactional(readOnly = true)
    public List<UserEntityDto> getActiveUsers(){
        return userRepository.findByIsActiveTrue()
                .stream()
                .map(user -> modelMapper.map(user, UserEntityDto.class))
                .toList();
    }

    // update user
    @Override
    @Transactional
    public UserEntityDto updateUser(Long userId, UpdateUserRequestDto request){
        log.info("Updating user ID: {}", userId);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User not found with id: " + userId));

        // update email id provided
        if(request.getEmail() != null && !request.getEmail().equals(user.getEmail())){
            if(userRepository.existsByEmailIgnoreCase(request.getEmail())){
                throw new DuplicateResourceException("Email already in user");
            }
            user.setEmail(request.getEmail());
        }

        UserEntity update = userRepository.save(user);
        log.info("User updated successfully: ID {}", userId);

        return modelMapper.map(update, UserEntityDto.class);
    }

    // deactivate user
    @Override
    @Transactional
    public void deactivateUser(Long userId) {
        log.info("Deactivating user ID: {}", userId);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.deactivate();
        userRepository.save(user);

        log.info("User deactivated successfully: ID {}", userId);
    }

    // reactivate user
    @Override
    @Transactional
    public void reactivateUser(Long userId) {
        log.info("Reactivating user ID: {}", userId);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.activate();
        userRepository.save(user);

        log.info("User reactivated successfully: ID {}", userId);
    }

    // deleted user
    @Override
    @Transactional
    public void deleteUser(Long userId){
        log.info("Hard deleting user ID: {}", userId);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User not found with id: " + userId));

        if(user.isActive()){
            log.warn("Cannot delete active user: ID {}", userId);
            throw new IllegalArgumentException("Cannot delete active user, Deactivate first");
        }

        userRepository.delete(user);
        log.warn("User deleted user: ID {}", userId);
    }

    // ==================== Role Management ====================
    // update user role
    @Override
    @Transactional
    public void updateUserRole(Long userId, Role newRole){
        log.info("Updating role user role: {}", userId);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User not found with id: " + userId));

        user.setRole(newRole);
        userRepository.save(user);
        log.info("User role is updated successfully : {}", user.getUserId());
    }

    // get user by role
    @Override
    @Transactional(readOnly = true)
    public List<UserEntityDto> getUserByRole(Role role){
        return userRepository.findByRole(role)
                .stream()
                .map(user -> modelMapper.map(user, UserEntityDto.class))
                .toList();
    }

    // user by role
    @Override
    @Transactional(readOnly = true)
    public long countUserByRole(Role role){
        return userRepository.countByRole(role);
    }

    // ==================== Search & Verification ====================
    // search user
    @Override
    @Transactional(readOnly = true)
    public List<UserEntityDto> searchUsers(String searchTerm){
        return userRepository.searchByUsernameOrEmail(searchTerm)
                .stream()
                .map(user->modelMapper.map(user, UserEntityDto.class))
                .toList();
    }

    // check user username is available or not
    @Override
    @Transactional(readOnly = true)
    public boolean isUsernameAvailable(String username){
        return !userRepository.existsByUsernameIgnoreCase(username);
    }

    // check user username is available or not

    @Override
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email){
        return !userRepository.existsByEmailIgnoreCase(email);
    }

    // get user entity by username
    @Override
    @Transactional(readOnly = true)
    public UserEntity getUserEntityByUsername(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(()->new ResourceNotFoundException("User with this username not found: " + username));
    }
}
