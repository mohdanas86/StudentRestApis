package com.anas.StudentRestApis.Dto;

import com.anas.StudentRestApis.Entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UpdateUserRequestDto - User information returned in API responses
 * Security: Never includes passwordHash, even if explicitly requested
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequestDto {
    private Long userId;
    private String username;
    private String email;
    private Role role;
    private boolean isActive;
    private Long teacherId;
}
