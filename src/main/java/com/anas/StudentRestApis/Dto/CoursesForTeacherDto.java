package com.anas.StudentRestApis.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * CoursesForTeacherDto - DTO for courses assigned to a teacher.
 *
 * Used when retrieving all courses for a specific teacher.
 * Contains complete course details including college information and assignment
 * timestamp.
 *
 * Typical usage:
 * - Response when getting all courses for a teacher (GET
 * /teachers/{id}/courses)
 * - Includes assignment metadata (assignedAt) along with course details
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoursesForTeacherDto {

    /** Course unique identifier */
    private Long courseId;

    /** Course name/title for display */
    private String courseName;

    /** Course code (e.g., CS101, MATH201) - unique within college */
    private String courseCode;

    /** Detailed description of the course content and objectives */
    private String description;

    /** College/Institution identifier offering this course */
    private Long collegeId;

    /** College name for display purposes */
    private String collegeName;

    /** Number of credit hours/units for the course */
    private Double credits;

    /** Timestamp when teacher was assigned to this course */
    private LocalDateTime assignedAt;
}
