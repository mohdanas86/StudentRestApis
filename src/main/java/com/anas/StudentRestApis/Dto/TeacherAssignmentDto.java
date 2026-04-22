package com.anas.StudentRestApis.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * TeacherAssignmentDto - DTO for teacher information in assignment context.
 *
 * Used when retrieving teachers assigned to a course or in course roster
 * responses.
 * Contains teacher details plus assignment-specific metadata.
 *
 * Typical usage:
 * - Response when getting all teachers for a course (GET
 * /courses/{id}/teachers)
 * - Component of course roster responses
 * - Contains full teacher profile information for display
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeacherAssignmentDto {

    /** Teacher unique identifier */
    private Long teacherId;

    /** Teacher first name */
    private String firstName;

    /** Teacher last name */
    private String lastName;

    /** Teacher email address for contact */
    private String email;

    /** Teacher's area of expertise or specialization (e.g., Java Development) */
    private String specialization;

    /** Teacher's employee ID (organizational identifier) */
    private String employeeId;

    /** College/Institution identifier where teacher is employed */
    private Long collegeId;

    /** College name for display purposes */
    private String collegeName;

    /** Timestamp when teacher was assigned to this course */
    private LocalDateTime assignedAt;

}
