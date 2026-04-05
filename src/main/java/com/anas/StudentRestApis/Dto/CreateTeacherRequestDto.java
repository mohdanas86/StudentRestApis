package com.anas.StudentRestApis.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for CreateTeacherRequestDto - Contains teacher information for adding new teacher
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateTeacherRequestDto {

    @NotBlank(message = "Employee id is required")
    private String employeeId;

    @NotBlank(message = "First name is required")
    @Size(min = 3)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 3)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @NotBlank(message = "Specialization is required")
    private String specialization;

    @NotNull(message = "College ID is required")
    private long collegeId;
}
