package com.anas.StudentRestApis.Dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * AssignmentCheckDto - DTO for checking if a teacher is assigned to a course.
 *
 * Used when checking teacher-course assignment status.
 * Contains assignment details when teacher is assigned, or just status when not
 * assigned.
 *
 * Typical usage:
 * - Response when checking assignment (GET /courses/{id}/teachers/{id})
 * - Fields assignedAt, assignedBy are included only when isAssigned is true
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssignmentCheckDto {

    /** Whether the teacher is assigned to the course */
    private Boolean isAssigned;

    /** Course unique identifier */
    private Long courseId;

    /** Teacher unique identifier */
    private Long teacherId;

    /** Timestamp when the assignment was created (only if isAssigned=true) */
    private LocalDateTime assignedAt;

    /** Username who created this assignment (only if isAssigned=true) */
    private String assignedBy;
}
