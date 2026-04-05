package com.anas.StudentRestApis.Dto;

import com.anas.StudentRestApis.Entity.TeacherEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for TeacherEntity - Contains teacher information for API responses
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeacherEntityDto {
    private long teacherId;
    private String employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private String specialization;
    private Long collegeId;
    private String collegeName;
    private List<String> courseNames;
    private Boolean isActive;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}