package com.anas.StudentRestApis.Dto;

import com.anas.StudentRestApis.Entity.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * UserEntityDto - User information returned in API responses
 *
 * Security: Never includes passwordHash, even if explicitly requested
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntityDto {

    private Long userId;
    private String username;
    private String email;
    private Role role;

    @JsonProperty("is_active")
    private boolean isActive;

    @JsonProperty("teacher_id")
    private Long teacherId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
