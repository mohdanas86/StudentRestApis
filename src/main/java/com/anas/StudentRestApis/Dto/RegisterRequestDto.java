package com.anas.StudentRestApis.Dto;

import com.anas.StudentRestApis.Entity.Role;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RegisterRequestDto - User registration data
 *
 * Validation Strategy:
 * - Strong password enforcement (8+ chars, mixed case, numbers)
 * - Email format validation
 * - Username length constraints
 * - Role restricted to TEACHER/ADMIN (STUDENT created via invitation)
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDto {


    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(
            regexp = "^[a-zA-Z0-9_-]+$",
            message = "Username can only contain letters, numbers, underscores, and hyphens"
    )
    private String username;

    @NotBlank(message = "Password is required")
    @Size(
            min = 8,
            max = 128,
            message = "Password must be between 8 and 128 characters"
    )
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[a-zA-Z\\d@$!%*?&]+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character (@$!%*?&)"
    )
    private String password;

    @NotNull(message = "Role is required")
    private Role role; // Only ADMIN and TEACHER can self-register

    /**
     * Optional: Link to existing teacher during registration
     * Used when converting a teacher to a system user
     */
    private Long teacherId;
}
