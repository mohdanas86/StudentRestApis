package com.anas.StudentRestApis.Controllers;

import com.anas.StudentRestApis.Common.ApiResponse;
import com.anas.StudentRestApis.Dto.CourseEntityDto;
import com.anas.StudentRestApis.Dto.CreateCourseRequestDto;
import com.anas.StudentRestApis.Dto.UpdateCourseRequestDto;
import com.anas.StudentRestApis.Exception.DuplicateResourceException;
import com.anas.StudentRestApis.Exception.ResourceNotFoundException;
import com.anas.StudentRestApis.Service.CourseServices;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CourseControllers - REST endpoints for course operations
 */
@Slf4j
@RestController
@RequestMapping("/courses")
@AllArgsConstructor
public class CourseControllers {

    private final CourseServices courseServices;

    /**
     * GET /courses - Retrieves all courses
     *
     * @return ResponseEntity with:
     *         - 200 OK: List of courses with full details
     *         - 204 No Content: If no course found
     *         - 500 Internal Server Error: If unexpected error occurs
     */
    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<List<CourseEntityDto>>> getAllCourses() {
        try {
            log.info("Fetching courses");
            List<CourseEntityDto> courses = courseServices.getAllCourses();

            log.info("Fetched successfully {} courses", courses.size());
            return ResponseEntity.ok(
                    ApiResponse.ok(courses, "Courses fetched successfully"));

        } catch (Exception e) {
            log.info("Failed to fetch courses");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch courses", "An error occurred while fetching courses"));
        }
    }

    /**
     * GET /courses/{coursesId} - Get course details
     *
     * @return ResponseEntity with:
     *         - 200 OK: college with full details
     *         - 404 Not Found: If college not found
     *         - 500 Internal Server Error: If unexpected error occurs
     */
    @GetMapping("/{courseId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<CourseEntityDto>> getCourseByCourseId(@PathVariable long courseId) {
        try {
            log.info("Fetching course");

            CourseEntityDto course = courseServices.getCourseByCourseId(courseId);
            log.info("Course found with id : {}", course.getCourseId());
            return ResponseEntity.ok(
                    ApiResponse.ok(course, "Course fetched successfully"));
        } catch (ResourceNotFoundException e) {
            log.warn("Course not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Not Found", e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to fetch course", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch course", "An error occurred while fetching course"));
        }
    }

    /**
     * POST /courses - Create new course
     *
     * @return ResponseEntity with:
     *         - 201 Created: course with full details
     *         - 400 Bad Request: If validation fails or duplicate course code
     *         - 500 Internal Server Error: If unexpected error occurs
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<CourseEntityDto>> createCourse(
            @Valid @RequestBody CreateCourseRequestDto request) {
        try {
            log.info("Creating a new course");
            CourseEntityDto createCourse = courseServices.createCourse(request);
            log.info("Course created successfully: {}", createCourse.getCourseId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.ok(createCourse, "Course created successfully"));
        } catch (ResourceNotFoundException e) {
            log.warn("College not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Not Found", e.getMessage()));
        } catch (DuplicateResourceException e) {
            log.warn("Duplicate course code: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Duplicate Resource", e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to create course", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create course", "An error occurred while creating course"));
        }
    }

    /**
     * PUT /courses/{courseId} - Update a course
     *
     * @param courseId courseId to update
     * @param request  UpdateCourseRequestDto with updated course details
     * @return ResponseEntity with:
     *         - 200 OK: Updated course with full details
     *         - 404 Not Found: If not found
     *         - 400 Bad Request: If validation fails
     *         - 500 Internal Server Error: If unexpected error occurs
     */
    @PutMapping("/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<CourseEntityDto>> updateCourseByCourseId(
            @PathVariable long courseId,
            @Valid @RequestBody UpdateCourseRequestDto request) {
        try {
            log.info("Updating course");
            CourseEntityDto updateCourse = courseServices.updateCourse(courseId, request);
            log.info("Course updated successfully: {}", updateCourse.getCourseId());
            return ResponseEntity.ok(
                    ApiResponse.ok(updateCourse, "Course updated successfully"));
        } catch (ResourceNotFoundException e) {
            log.warn("Course or College not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Not Found", e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to update course", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update course", "An error occurred while updating course"));
        }
    }

    /**
     * DELETE /courses/{courseId} - Delete a course by courseId
     *
     * @param courseId courseId to delete
     * @return ResponseEntity with:
     *         - 200 OK: Deletion successful
     *         - 404 Not Found: If not found
     *         - 500 Internal Server Error: If unexpected error occurs
     */
    @DeleteMapping("/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteCourseById(@PathVariable long courseId) {
        try {
            log.info("Deleting course");
            courseServices.deleteCourseByCourseId(courseId);
            log.info("Course deleted successfully");
            return ResponseEntity.ok(
                    ApiResponse.ok(null, "Course deleted successfully"));
        } catch (ResourceNotFoundException e) {
            log.warn("Course not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Not Found", e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to delete course", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete course", "An error occurred while deleting course"));
        }
    }
}
