package com.anas.StudentRestApis.Dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for UpdateCourseRequestDto - Contains course information for updating a
 * course
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCourseRequestDto {

    @Size(min = 3)
    private String courseCode;

    @Size(min = 3)
    private String courseName;

    @DecimalMin("0.0")
    private double credits;

    private Long collegeId;
}
