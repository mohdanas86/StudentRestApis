package com.anas.StudentRestApis.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * CourseTeacherAssignmentDto - DTO for course-teacher assignment response.
 *
 * Used for REST API responses when assigning a teacher to a course or
 * retrieving assignment details. Contains all relevant information about
 * the assignment including course, teacher, and metadata.
 *
 * Typical usage:
 * - Response when creating an assignment (POST /courses/{id}/teachers/{id})
 * - Response when querying assignment details
 * - Contains both course and teacher information in a single object
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseTeacherAssignmentDto {

    /** Unique identifier for this assignment */
    private Long assignmentId;

    /** Course identifier - links to the course being taught */
    private Long courseId;

    /** Course name for display purposes */
    private String courseName;

    /** Teacher identifier - links to the teacher */
    private Long teacherId;

    /** Teacher full name (firstName + lastName) for display */
    private String teacherName;

    /** Teacher email contact information */
    private String teacherEmail;

    /** Timestamp when the assignment was created */
    private LocalDateTime assignedAt;

    /** Username of the admin who created this assignment (for audit trail) */
    private String assignedBy;
}
