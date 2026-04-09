package com.anas.StudentRestApis.Dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for UpdateCollegeRequestDto - Contains college information for updating a college
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCollegeRequestDto {

    @Size(min = 3)
    private String collegeCode;

    @Size(min = 3)
    private String collegeName;

    @Size(min = 3)
    private String deanName;
}
