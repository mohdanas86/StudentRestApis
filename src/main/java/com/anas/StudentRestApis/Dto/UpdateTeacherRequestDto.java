package com.anas.StudentRestApis.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for UpdateTeacherRequestDto - Contains teacher information for updating a
 * teacher
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTeacherRequestDto {

    private String employeeId;

    @Size(min = 3)
    private String firstName;

    @Size(min = 3)
    private String lastName;

    @Email
    private String email;

    private String specialization;

    private Boolean isActive;
}
