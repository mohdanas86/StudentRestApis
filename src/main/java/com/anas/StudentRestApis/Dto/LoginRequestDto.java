package com.anas.StudentRestApis.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LoginRequestDto - Credentials for user authentication
 *
 * Security Notes:
 * - Both fields are required
 * - Passwords are validated for minimum length
 * - Clear error messages don't reveal if username exists
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "User must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
