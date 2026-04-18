package com.anas.StudentRestApis.Dto;

import com.anas.StudentRestApis.Entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * LoginResponseDto - Response returned after successful login
 *
 * Security: Token is the only sensitive data, sent in response body
 * (not in Set-Cookie with HttpOnly flag - stateless API design)
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {

    private String accessToken;

    private String refreshToken;

    private String tokenType = "Bearer";

    private long expiresIn; // seconds

    private UserInfoDto user;

    // ==================== Nested DTO ====================
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInfoDto{
        private Long userId;
        private String username;
        private String email;
        private Role role;
        private LocalDateTime createdAt;
    }
}
