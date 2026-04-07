package com.anas.StudentRestApis.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for CreateCollegeRequestDto - Contains college information for adding new college
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateCollegeRequestDto {

    @NotBlank(message = "College code is required")
    private String collegeCode;

    @NotBlank(message = "College name is required")
    private String collegeName;

    @NotBlank(message = "Dean name is required")
    private String deanName;
}
