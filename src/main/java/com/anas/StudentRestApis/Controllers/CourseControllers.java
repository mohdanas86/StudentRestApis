package com.anas.StudentRestApis.Controllers;

import com.anas.StudentRestApis.Common.ApiResponse;
import com.anas.StudentRestApis.Dto.CourseEntityDto;
import com.anas.StudentRestApis.Service.CourseServices;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<List<CourseEntityDto>>> getAllCourses(){
        try{
            log.info("Fetching courses");
            List<CourseEntityDto> courses = courseServices.getAllCourses();

            log.info("Fetched successfully {} courses", courses.size());
            return ResponseEntity.ok(
                    ApiResponse.ok(courses, "Courses fetched cuccessfully")
            );

        } catch (Exception e) {
            log.info("Failed to fetch courses");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch courses", "An error occurred while fetching courses"));
        }
    }

    /**
     * GET /colleges/{collegeId} - Get college details
     *
     * @return ResponseEntity with:
     *         - 200 OK: college with full details
     *         - 404 Not Found: If college not found
     *         - 500 Internal Server Error: If unexpected error occurs
     */


    /**
     * POST /colleges - Create new college
     *
     * @return ResponseEntity with:
     *         - 201 Created: college with full details
     *         - 400 Bad Request: If validation fails or duplicate college code
     *         - 500 Internal Server Error: If unexpected error occurs
     */


    /**
     * PUT /colleges/{collegeId} - Update a college
     *
     * @param collegeId collegeId to update
     * @param request   UpdateCollegeRequestDto with updated college details
     * @return ResponseEntity with:
     *         - 200 OK: Updated college with full details
     *         - 404 Not Found: If college not found
     *         - 400 Bad Request: If validation fails
     *         - 500 Internal Server Error: If unexpected error occurs
     */


    /**
     * DELETE /colleges/{collegeId} - Delete a college by ID
     *
     * @param collegeId CollegeId to delete
     * @return ResponseEntity with:
     *         - 200 OK: Deletion successful
     *         - 404 Not Found: If college not found
     *         - 500 Internal Server Error: If unexpected error occurs
     */

}
