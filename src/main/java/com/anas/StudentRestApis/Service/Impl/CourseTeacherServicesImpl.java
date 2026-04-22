package com.anas.StudentRestApis.Service.Impl;

import com.anas.StudentRestApis.Dto.CourseEntityDto;
import com.anas.StudentRestApis.Dto.CourseRosterDto;
import com.anas.StudentRestApis.Dto.CourseTeacherAssignmentDto;
import com.anas.StudentRestApis.Dto.TeacherAssignmentDto;
import com.anas.StudentRestApis.Dto.AssignmentCheckDto;
import com.anas.StudentRestApis.Dto.CoursesForTeacherDto;
import com.anas.StudentRestApis.Entity.CourseEntity;
import com.anas.StudentRestApis.Entity.CourseTeacherEntity;
import com.anas.StudentRestApis.Entity.TeacherEntity;
import com.anas.StudentRestApis.Exception.DuplicateResourceException;
import com.anas.StudentRestApis.Exception.ResourceNotFoundException;
import com.anas.StudentRestApis.Exception.ValidationException;
import com.anas.StudentRestApis.Repository.CourseRepository;
import com.anas.StudentRestApis.Repository.CourseTeacherRepository;
import com.anas.StudentRestApis.Repository.TeacherRepository;
import com.anas.StudentRestApis.Service.CourseTeacherServices;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * CourseTeacherServicesImpl - Implementation of CourseTeacherServices
 * interface.
 *
 * Handles business logic for managing course-teacher assignments including:
 * - Creating new assignments with comprehensive validation
 * - Removing teacher assignments from courses
 * - Querying assigned teachers for courses
 * - Querying assigned courses for teachers
 * - Generating complete course roster reports
 *
 * All CRUD operations are transactional (@Transactional) and logged for audit
 * trails.
 * Method-level authorization (@PreAuthorize) enforces role-based access
 * control.
 */
@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class CourseTeacherServicesImpl implements CourseTeacherServices {

    private final CourseTeacherRepository courseTeacherRepository;
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final ModelMapper modelMapper;

    /**
     * Assign a teacher to a course with comprehensive validation.
     *
     * Validates:
     * - Course exists in database
     * - Teacher exists in database
     * - Teacher is not already assigned to this course (no duplicates)
     * - Teacher is in active status
     *
     * Authorization: ADMIN only
     *
     * @param courseId  the ID of the course
     * @param teacherId the ID of the teacher
     * @return CourseTeacherAssignmentDto with assignment details
     * @throws ResourceNotFoundException  if course or teacher not found
     * @throws DuplicateResourceException if teacher already assigned to course
     * @throws ValidationException        if teacher is inactive
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public CourseTeacherAssignmentDto assignTeacherToCourse(Long courseId, Long teacherId) {
        log.debug("Attempting to assign teacher {} to course {}", teacherId, courseId);

        // Validation: Check course exists
        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course with ID " + courseId + " not found"));
        log.debug("Course found: {}", course.getCourseName());

        // Validation: Check teacher exists
        TeacherEntity teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Teacher with ID " + teacherId + " not found"));
        log.debug("Teacher found: {} {}", teacher.getFirstName(), teacher.getLastName());

        // Validation: Check not already assigned (prevent duplicates)
        if (courseTeacherRepository.existsByCourseIdAndTeacherId(courseId, teacherId)) {
            log.warn("Duplicate assignment attempt: Teacher {} already assigned to course {}", teacherId, courseId);
            throw new DuplicateResourceException(
                    "Teacher is already assigned to this course");
        }

        // Validation: Check teacher is active
        if (!teacher.getIsActive()) {
            log.warn("Cannot assign inactive teacher {} to course {}", teacherId, courseId);
            throw new ValidationException(
                    "Cannot assign inactive teacher to this course");
        }

        // Create new assignment entity
        CourseTeacherEntity assignment = new CourseTeacherEntity();
        assignment.setCourse(course);
        assignment.setTeacher(teacher);
        assignment.setAssignedBy(getCurrentUsername());

        // Persist assignment to database
        CourseTeacherEntity saved = courseTeacherRepository.save(assignment);
        log.info("Successfully assigned teacher {} to course {}", teacherId, courseId);

        // Convert to DTO using ModelMapper
        CourseTeacherAssignmentDto dto = modelMapper.map(saved, CourseTeacherAssignmentDto.class);
        dto.setCourseId(saved.getCourse().getCourseId());
        dto.setCourseName(saved.getCourse().getCourseName());
        dto.setTeacherId(saved.getTeacher().getTeacherId());
        dto.setTeacherName(saved.getTeacher().getFirstName() + " " + saved.getTeacher().getLastName());
        dto.setTeacherEmail(saved.getTeacher().getEmail());

        return dto;
    }

    /**
     * Remove a teacher from a course.
     *
     * Validates that the assignment exists before deletion.
     * Uses soft delete pattern if implemented, otherwise hard delete.
     *
     * Authorization: ADMIN only
     *
     * @param courseId  the ID of the course
     * @param teacherId the ID of the teacher
     * @throws ResourceNotFoundException if assignment does not exist
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void removeTeacherFromCourse(Long courseId, Long teacherId) {
        log.debug("Attempting to remove teacher {} from course {}", teacherId, courseId);

        // Validation: Check assignment exists
        CourseTeacherEntity assignment = courseTeacherRepository
                .findByCourseIdAndTeacherId(courseId, teacherId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "This teacher is not assigned to this course"));

        // Delete assignment from database
        courseTeacherRepository.delete(assignment);
        log.info("Successfully removed teacher {} from course {}", teacherId, courseId);
    }

    /**
     * Get all teachers assigned to a specific course.
     *
     * Retrieves complete teacher information for all assignments to the course.
     * Returns empty list if no teachers assigned.
     *
     * Authorization: Public (permitAll)
     *
     * @param courseId the ID of the course
     * @return List of TeacherAssignmentDto containing teacher details
     * @throws ResourceNotFoundException if course does not exist
     */
    @Override
    public List<TeacherAssignmentDto> getTeachersForCourse(Long courseId) {
        log.debug("Fetching all teachers for course {}", courseId);

        // Validation: Check course exists
        if (!courseRepository.existsById(courseId)) {
            log.warn("Course not found: {}", courseId);
            throw new ResourceNotFoundException(
                    "Course with ID " + courseId + " not found");
        }

        // Retrieve all assignments for the course
        List<CourseTeacherEntity> assignments = courseTeacherRepository
                .findByCourseId(courseId);

        log.debug("Found {} teachers assigned to course {}", assignments.size(), courseId);

        // Convert to DTO using ModelMapper
        return assignments.stream()
                .map(assignment -> modelMapper.map(assignment.getTeacher(), TeacherAssignmentDto.class))
                .toList();
    }

    /**
     * Get all courses assigned to a specific teacher.
     *
     * Retrieves complete course information for all assignments to the teacher.
     * Returns empty list if teacher has no assigned courses.
     *
     * Authorization: Public (permitAll)
     *
     * @param teacherId the ID of the teacher
     * @return List of CoursesForTeacherDto containing course details with
     *         assignment timestamp
     * @throws ResourceNotFoundException if teacher does not exist
     */
    @Override
    public List<CoursesForTeacherDto> getCoursesForTeacher(Long teacherId) {
        log.debug("Fetching all courses for teacher {}", teacherId);

        // Validation: Check teacher exists
        if (!teacherRepository.existsById(teacherId)) {
            log.warn("Teacher not found: {}", teacherId);
            throw new ResourceNotFoundException(
                    "Teacher with ID " + teacherId + " not found");
        }

        // Retrieve all assignments for the teacher
        List<CourseTeacherEntity> assignments = courseTeacherRepository
                .findByTeacherId(teacherId);

        log.debug("Found {} courses assigned to teacher {}", assignments.size(), teacherId);

        // Convert to DTO and return with assignment details
        return assignments.stream()
                .map(assignment -> {
                    CoursesForTeacherDto dto = modelMapper.map(assignment.getCourse(), CoursesForTeacherDto.class);
                    dto.setAssignedAt(assignment.getAssignedAt());
                    // Set college information from course relationship
                    if (assignment.getCourse().getCollege() != null) {
                        dto.setCollegeId(assignment.getCourse().getCollege().getCollegeId());
                        dto.setCollegeName(assignment.getCourse().getCollege().getCollegeName());
                    }
                    return dto;
                })
                .toList();
    }

    /**
     * Check if a teacher is assigned to a course with full details.
     *
     * Returns assignment status and details if assigned.
     * Returns only status if not assigned.
     *
     * Authorization: Public (permitAll)
     *
     * @param courseId  the ID of the course
     * @param teacherId the ID of the teacher
     * @return AssignmentCheckDto with assignment status and details
     */
    @Override
    public AssignmentCheckDto checkAssignment(Long courseId, Long teacherId) {
        log.debug("Checking if teacher {} is assigned to course {}", teacherId, courseId);

        AssignmentCheckDto result = new AssignmentCheckDto();
        result.setCourseId(courseId);
        result.setTeacherId(teacherId);

        // Check if assignment exists
        var assignment = courseTeacherRepository.findByCourseIdAndTeacherId(courseId, teacherId);

        if (assignment.isPresent()) {
            result.setIsAssigned(true);
            result.setAssignedAt(assignment.get().getAssignedAt());
            result.setAssignedBy(assignment.get().getAssignedBy());
        } else {
            result.setIsAssigned(false);
        }

        return result;
    }

    /**
     * Get complete course roster with all assigned teachers.
     *
     * Generates a comprehensive report containing course details and
     * all assigned teachers. Useful for administrative views and dashboards.
     *
     * Authorization: Public (permitAll)
     *
     * @param courseId the ID of the course
     * @return CourseRosterDto containing full course and teacher information
     * @throws ResourceNotFoundException if course does not exist
     */
    @Override
    public CourseRosterDto getCourseRoster(Long courseId) {
        log.debug("Generating roster for course {}", courseId);

        // Validation: Check course exists and retrieve
        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course with ID " + courseId + " not found"));

        // Get all teachers assigned to this course
        List<TeacherAssignmentDto> teachers = getTeachersForCourse(courseId);

        // Map course to DTO and set teachers
        CourseRosterDto roster = modelMapper.map(course, CourseRosterDto.class);
        roster.setTeachers(teachers);
        roster.setTotalTeachers(teachers.size());

        log.info("Generated roster for course {} with {} teachers", courseId, teachers.size());
        return roster;
    }

    /**
     * Helper method to extract current authenticated user's username.
     *
     * Gets the username from Spring Security's SecurityContextHolder.
     * Used for audit trail when recording who made an assignment.
     *
     * @return the username of the currently authenticated user
     */
    private String getCurrentUsername() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }
}
