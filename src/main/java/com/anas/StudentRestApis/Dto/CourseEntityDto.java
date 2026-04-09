package com.anas.StudentRestApis.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for CourseEntityDto - Contains courses information for API responses
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseEntityDto {

    private long courseId;
    private String courseCode;
    private String courseName;
    private double credits;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
