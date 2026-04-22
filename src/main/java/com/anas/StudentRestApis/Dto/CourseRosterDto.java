package com.anas.StudentRestApis.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * CourseRosterDto - Complete course information with assigned teachers.
 *
 * Provides a comprehensive view of a course including all assigned teachers
 * and course metadata. Used for retrieving complete course roster information.
 *
 * Typical usage:
 * - Response when getting course roster (GET /courses/{id}/roster)
 * - Contains both course details and teacher list in single response
 * - Used for administrative dashboards and course management interfaces
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseRosterDto {

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

    /** List of all teachers assigned to teach this course */
    private List<TeacherAssignmentDto> teachers;

    /**
     * Total count of teachers assigned (convenience field, equals teachers.size())
     */
    private Integer totalTeachers;

    /** Timestamp when the course was created in the system */
    private LocalDateTime createdAt;

    /** Timestamp of last modification to the course */
    private LocalDateTime updatedAt;
}
