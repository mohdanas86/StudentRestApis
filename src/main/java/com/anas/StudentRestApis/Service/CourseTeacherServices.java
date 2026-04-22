package com.anas.StudentRestApis.Service;

import com.anas.StudentRestApis.Dto.AssignmentCheckDto;
import com.anas.StudentRestApis.Dto.CoursesForTeacherDto;
import com.anas.StudentRestApis.Dto.CourseRosterDto;
import com.anas.StudentRestApis.Dto.CourseTeacherAssignmentDto;
import com.anas.StudentRestApis.Dto.TeacherAssignmentDto;
import com.anas.StudentRestApis.Exception.DuplicateResourceException;
import com.anas.StudentRestApis.Exception.ResourceNotFoundException;
import com.anas.StudentRestApis.Exception.ValidationException;

import java.util.List;

/**
 * CourseTeacherServices - Business logic layer for course-teacher assignments.
 *
 * Manages the many-to-many relationship between courses and teachers.
 * Handles assignment creation, deletion, and queries with comprehensive
 * validation.
 *
 * Authorization:
 * - assignTeacherToCourse: ADMIN only
 * - removeTeacherFromCourse: ADMIN only
 * - All query methods: Public (permitAll)
 *
 * Validation:
 * - Course and teacher must exist
 * - No duplicate assignments allowed
 * - Teacher must be active
 * - Course must be active
 *
 * Exceptions:
 * - ResourceNotFoundException: When course or teacher not found
 * - DuplicateResourceException: When trying to assign already-assigned teacher
 * - ValidationException: When validation fails (inactive status, etc.)
 */
public interface CourseTeacherServices {
    /**
     * Assign a teacher to a course
     * 
     * @param courseId  - ID of the course
     * @param teacherId - ID of the teacher
     * @return CourseTeacherAssignmentDto with assignment details
     * @throws ResourceNotFoundException of course or teacher not found
     * @throws ValidationException       if already assigned or invalid state
     */
    CourseTeacherAssignmentDto assignTeacherToCourse(Long courseId, Long teacherId);

    /**
     * Remove a teacher from a course
     * 
     * @param courseId  - ID of the course
     * @param teacherId - ID of the teacher
     * @throws ResourceNotFoundException if assignment not found
     */
    void removeTeacherFromCourse(Long courseId, Long teacherId);

    /**
     * Get all teachers assigned to a course
     * 
     * @param courseId - ID of the course
     * @return List of TeacherAssignmentDto
     * @throws ResourceNotFoundException if course not found
     */
    List<TeacherAssignmentDto> getTeachersForCourse(Long courseId);

    /**
     * Get all courses assigned to a teacher
     * 
     * @param teacherId - ID of the teacher
     * @return List of CoursesForTeacherDto
     * @throws ResourceNotFoundException if teacher not found
     */
    List<CoursesForTeacherDto> getCoursesForTeacher(Long teacherId);

    /**
     * Check if teacher is assigned to course
     * 
     * @param courseId  - ID of the course
     * @param teacherId - ID of the teacher
     * @return AssignmentCheckDto with assignment status
     */
    AssignmentCheckDto checkAssignment(Long courseId, Long teacherId);

    /**
     * Get complete course roster with all teachers
     * 
     * @param courseId - ID of the course
     * @return CourseRosterDto with complete roster information
     * @throws ResourceNotFoundException if course not found
     */
    CourseRosterDto getCourseRoster(Long courseId);
}
