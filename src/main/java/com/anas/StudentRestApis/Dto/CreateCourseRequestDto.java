package com.anas.StudentRestApis.Dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for CreateCourseRequestDto - Contains courses information to create a new course
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateCourseRequestDto {

    @NotBlank(message = "Course code is required")
    private String courseCode;

    @NotBlank(message = "Course name is required")
    private String courseName;

    @DecimalMin("0.0")
    private double credits;
}
