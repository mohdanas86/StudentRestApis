package com.anas.StudentRestApis.Controllers;

import com.anas.StudentRestApis.Common.ApiResponse;
import com.anas.StudentRestApis.Dto.AssignmentCheckDto;
import com.anas.StudentRestApis.Dto.CoursesForTeacherDto;
import com.anas.StudentRestApis.Dto.CourseRosterDto;
import com.anas.StudentRestApis.Dto.CourseTeacherAssignmentDto;
import com.anas.StudentRestApis.Dto.TeacherAssignmentDto;
import com.anas.StudentRestApis.Service.CourseTeacherServices;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * CourseTeacherControllers - REST endpoints for course-teacher assignments
 * <p>
 * Provides REST API for managing many-to-many relationships between courses and
 * teachers.
 * Includes endpoints for assigning/removing teachers, querying assignments, and
 * generating rosters.
 * <p>
 * All endpoints include comprehensive error handling with try-catch blocks.
 * Authorization is enforced using @PreAuthorize annotations:
 * - assignTeacherToCourse: ADMIN only
 * - removeTeacherFromCourse: ADMIN only
 * - All GET endpoints: Public (permitAll)
 */
@Slf4j
@RestController
@RequestMapping("/courses")
@AllArgsConstructor
public class CourseTeacherControllers {

    private final CourseTeacherServices courseTeacherServices;

    /**
     * POST /{courseId}/teachers/{teacherId} - Assign a teacher to a course
     *
     * @param courseId  the ID of the course
     * @param teacherId the ID of the teacher
     * @return ResponseEntity with:
     * - 201 Created: Assignment details with full information
     * - 404 Not Found: If course or teacher not found
     * - 409 Conflict: If teacher already assigned to course
     * - 500 Internal Server Error: If unexpected error occurs
     */
    @PostMapping("/{courseId}/teachers/{teacherId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CourseTeacherAssignmentDto>> assignTeacherToCourse(
            @PathVariable Long courseId,
            @PathVariable Long teacherId) {
        try {
            log.info("Assigning teacher {} to course {}", teacherId, courseId);
            CourseTeacherAssignmentDto result = courseTeacherServices
                    .assignTeacherToCourse(courseId, teacherId);

            log.info("Successfully assigned teacher {} to course {}", teacherId, courseId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.ok(result, "Teacher successfully assigned to course"));
        } catch (Exception e) {
            log.error("Error assigning teacher to course", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to assign teacher", e.getMessage()));
        }
    }

    /**
     * DELETE /{courseId}/teachers/{teacherId} - Remove a teacher from a course
     *
     * @param courseId  the ID of the course
     * @param teacherId the ID of the teacher
     * @return ResponseEntity with:
     * - 200 OK: Success message
     * - 404 Not Found: If assignment not found
     * - 500 Internal Server Error: If unexpected error occurs
     */
    @DeleteMapping("/{courseId}/teachers/{teacherId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> removeTeacherFromCourse(
            @PathVariable Long courseId,
            @PathVariable Long teacherId) {
        try {
            log.info("Removing teacher {} from course {}", teacherId, courseId);
            courseTeacherServices.removeTeacherFromCourse(courseId, teacherId);

            log.info("Successfully removed teacher {} from course {}", teacherId, courseId);
            return ResponseEntity.ok(
                    ApiResponse.ok(null, "Teacher successfully removed from course"));
        } catch (Exception e) {
            log.error("Error removing teacher from course", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to remove teacher", e.getMessage()));
        }
    }

    /**
     * GET /{courseId}/teachers - Get all teachers for a course
     *
     * @param courseId the ID of the course
     * @return ResponseEntity with:
     * - 200 OK: List of teachers assigned to course
     * - 204 No Content: If no teachers assigned
     * - 404 Not Found: If course not found
     * - 500 Internal Server Error: If unexpected error occurs
     */
    @GetMapping("/{courseId}/teachers")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<List<TeacherAssignmentDto>>> getTeachersForCourse(
            @PathVariable Long courseId) {
        try {
            log.info("Fetching all teachers for course {}", courseId);
            List<TeacherAssignmentDto> teachers = courseTeacherServices
                    .getTeachersForCourse(courseId);

            if (teachers.isEmpty()) {
                log.info("No teachers found for course {}", courseId);
                return ResponseEntity.noContent().build();
            }

            log.info("Successfully fetched {} teachers for course {}", teachers.size(), courseId);
            return ResponseEntity.ok(
                    ApiResponse.ok(teachers, "Teachers retrieved successfully"));
        } catch (Exception e) {
            log.error("Error fetching teachers for course", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch teachers", e.getMessage()));
        }
    }

    /**
     * GET /{courseId}/teachers/{teacherId} - Check if teacher is assigned to course
     *
     * @param courseId  the ID of the course
     * @param teacherId the ID of the teacher
     * @return ResponseEntity with:
     * - 200 OK: Assignment status and details
     * - 500 Internal Server Error: If unexpected error occurs
     */
    @GetMapping("/{courseId}/teachers/{teacherId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<AssignmentCheckDto>> checkAssignment(
            @PathVariable Long courseId,
            @PathVariable Long teacherId) {
        try {
            log.info("Checking assignment for teacher {} in course {}", teacherId, courseId);
            AssignmentCheckDto result = courseTeacherServices
                    .checkAssignment(courseId, teacherId);

            log.info("Assignment check completed for teacher {} in course {}", teacherId, courseId);
            return ResponseEntity.ok(
                    ApiResponse.ok(result, "Assignment check completed"));
        } catch (Exception e) {
            log.error("Error checking assignment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to check assignment", e.getMessage()));
        }
    }

    /**
     * GET /{courseId}/roster - Get complete course roster with all teachers
     *
     * @param courseId the ID of the course
     * @return ResponseEntity with:
     * - 200 OK: Complete course roster with all teachers
     * - 404 Not Found: If course not found
     * - 500 Internal Server Error: If unexpected error occurs
     */
    @GetMapping("/{courseId}/roster")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<CourseRosterDto>> getCourseRoster(
            @PathVariable Long courseId) {
        try {
            log.info("Fetching roster for course {}", courseId);
            CourseRosterDto roster = courseTeacherServices.getCourseRoster(courseId);

            log.info("Successfully fetched roster for course {} with {} teachers", courseId,
                    roster.getTotalTeachers());
            return ResponseEntity.ok(
                    ApiResponse.ok(roster, "Roster retrieved successfully"));
        } catch (Exception e) {
            log.error("Error fetching course roster", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch roster", e.getMessage()));
        }
    }
}
