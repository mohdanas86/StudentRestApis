package com.anas.StudentRestApis.Controllers;

import com.anas.StudentRestApis.Common.ApiResponse;
import com.anas.StudentRestApis.Dto.*;
import com.anas.StudentRestApis.Entity.UserEntity;
import com.anas.StudentRestApis.Exception.InvalidCredentialsException;
import com.anas.StudentRestApis.Service.UserServices;
import com.anas.StudentRestApis.config.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * AuthenticationController - Public endpoints for authentication
 *
 * Endpoints:
 * - POST /api/v1/auth/login - User login
 * - POST /api/v1/auth/register - User registration (admin/teacher only)
 * - POST /api/v1/auth/refresh - Refresh access token
 * - POST /api/v1/auth/validate - Validate token (for debugging)
 *
 * These endpoints do NOT require authentication
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthenticationController {

        private final UserServices userServices;
        private final JwtTokenProvider jwtTokenProvider;

        // ==================== Login ====================

        /**
         * POST /api/v1/auth/login
         * User login with credentials
         * <p>
         * Success Response: 200 OK with access token
         * Error Responses: 400 Bad Request, 401 Unauthorized
         */
        @PostMapping("/login")
        public ResponseEntity<ApiResponse<LoginResponseDto>> login(
                        @Valid @RequestBody LoginRequestDto request) {
                log.info("Login attempt for username: {}", request.getUsername());

                try {
                        // Authenticate user (throws InvalidCredentialsException if fails)
                        LoginResponseDto authResponse = userServices.login(request);

                        // Get user entity for token generation
                        UserEntity user = userServices.getUserEntityByUsername(request.getUsername());

                        // Generate access
                        String accessToken = jwtTokenProvider.generateToken(
                                        user.getUsername(),
                                        user.getUserId(),
                                        user.getRole().name(),
                                        user.getEmail());

                        // Generate refresh token
                        String refreshToken = jwtTokenProvider.generateRefreshToken(
                                        user.getUsername(),
                                        user.getUserId());

                        // Build response
                        LoginResponseDto response = LoginResponseDto.builder()
                                        .accessToken(accessToken)
                                        .refreshToken(refreshToken)
                                        .tokenType("Bearer")
                                        .expiresIn(86400) // 24 hours
                                        .user(LoginResponseDto.UserInfoDto.builder()
                                                        .userId(user.getUserId())
                                                        .username(user.getUsername())
                                                        .email(user.getEmail())
                                                        .role(user.getRole())
                                                        .createdAt(user.getCreatedAt())
                                                        .build())
                                        .build();

                        log.info("Login successful for user: {}", request.getUsername());
                        return ResponseEntity.ok(
                                        ApiResponse.ok(response, "Login successfully"));

                } catch (InvalidCredentialsException e) {
                        log.warn("Login failed: {}", e.getMessage());
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body(ApiResponse.error("Authentication Failed", e.getMessage()));
                } catch (Exception e) {
                        log.error("Login error", e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponse.error("Login Error", "An error occurred during login"));
                }
        }

        // ==================== Register ====================

        /**
         * POST /api/v1/auth/register
         * new admin or teacher account
         * <p>
         * Success Response: 201 Created with user details
         * Error Responses: 400 Bad Request, 409 Conflict
         * <p>
         * Note: Students must be created through admin invitation system
         */
        @PostMapping("/register")
        public ResponseEntity<ApiResponse<UserEntityDto>> register(
                        @Valid @RequestBody RegisterRequestDto request) {
                log.info("Registration attempt for username: {}", request.getUsername());

                try {
                        UserEntityDto newUser = userServices.register(request);

                        log.info("Registration successful for user: {}", request.getUsername());

                        return ResponseEntity.ok(
                                        ApiResponse.ok(newUser, "User registered successfully"));
                } catch (IllegalArgumentException e) {
                        log.warn("Registration validation error: {}", e.getMessage());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(ApiResponse.error("Registration Error", e.getMessage()));
                } catch (Exception e) {
                        log.error("Registration error", e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponse.error("Registration Error",
                                                        "An error occurred during registration"));
                }
        }

        // ==================== Refresh Token ====================

        /**
         * POST /api/v1/auth/refresh
         * Get new access token using refresh token
         * <p>
         * Life cycle:
         * 1. User receives access token (24 hours) and refresh token (7 days) at login
         * 2. Access token expires, client makes refresh request
         * 3. Server validates refresh token and issues new access token
         * 4. User can continue without re-login
         * 5. Refresh token can also expire, requiring re-login
         */
        @PostMapping("/refresh")
        public ResponseEntity<ApiResponse<Map<String, Object>>> refreshToken(
                        @Valid @RequestBody RefreshTokenRequestDto request) {
                log.info("Token refresh attempt");

                try {
                        String token = request.getRefreshToken();
                        // Validate refresh token
                        if (!jwtTokenProvider.validateToken(token)) {
                                log.warn("Invalid or expired refresh token");
                                throw new InvalidCredentialsException("Refresh token is invalid or expired");
                        }

                        // Extract user info from token
                        String username = jwtTokenProvider.getUsernameFromToken(token);
                        Long userId = jwtTokenProvider.getUserIdFromToken(token);

                        // Get updated user info
                        UserEntity user = userServices.getUserEntityByUsername(username);

                        // Generate new access token
                        String newAccessToken = jwtTokenProvider.generateToken(
                                        user.getUsername(),
                                        user.getUserId(),
                                        user.getRole().name(),
                                        user.getEmail());

                        // Build response
                        Map<String, Object> response = new HashMap<>();
                        response.put("access_token", newAccessToken);
                        response.put("token_type", "Bearer");
                        response.put("expires_in", 86400); // 24 hours

                        log.info("Token refreshed successfully for user: {}", username);

                        return ResponseEntity.ok(
                                        ApiResponse.ok(response, "Token refreshed successfully"));
                } catch (InvalidCredentialsException e) {
                        log.warn("Token refresh failed: {}", e.getMessage());
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body(ApiResponse.error("Token Refresh Failed", e.getMessage()));
                } catch (Exception e) {
                        log.error("Token refresh error", e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponse.error("Token Refresh Error", "Could not refresh token"));
                }
        }

        // ==================== Token Validation (Debug) ====================

        /**
         * POST /api/v1/auth/validate
         * token and return claims (for debugging)
         * <p>
         * Request Body: {"token": "..."}
         * Response: Token claims and expiration info
         */
        @PostMapping("/validate")
        public ResponseEntity<ApiResponse<Map<String, Object>>> validateToken(
                        @RequestBody Map<String, String> request) {
                String token = request.get("token");

                if (token == null || token.isEmpty()) {
                        return ResponseEntity.badRequest()
                                        .body(ApiResponse.error("Invalid token",
                                                        "Token signature or expiration is invalid"));
                }

                try {
                        if (!jwtTokenProvider.validateToken(token)) {
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(ApiResponse.error("Invalid Token",
                                                                "Token signature or expiration is invalid"));
                        }

                        // extract and return claims
                        Map<String, Object> claims = new HashMap<>();
                        claims.put("username", jwtTokenProvider.getUsernameFromToken(token));
                        claims.put("userId", jwtTokenProvider.getUserIdFromToken(token));
                        claims.put("role", jwtTokenProvider.getRoleFromToken(token));
                        claims.put("email", jwtTokenProvider.getEmailFromToken(token));
                        claims.put("expiredAt", jwtTokenProvider.getTokenExpirationDate(token));
                        claims.put("secondsRemaining", jwtTokenProvider.getTimeUntilExpiration(token));

                        return ResponseEntity.ok(
                                        ApiResponse.ok(claims, "Token is valid"));
                } catch (Exception e) {
                        log.error("Token validation error", e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponse.error("Validation Error", e.getMessage()));
                }
        }
}
