package com.anas.StudentRestApis.Controllers;

import com.anas.StudentRestApis.Common.ApiResponse;
import com.anas.StudentRestApis.Dto.CreateTeacherRequestDto;
import com.anas.StudentRestApis.Dto.TeacherEntityDto;
import com.anas.StudentRestApis.Dto.UpdateTeacherRequestDto;
import com.anas.StudentRestApis.Service.TeacherServices;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TeacherControllers - REST endpoints for teacher operations
 */
@Slf4j
@RestController
@RequestMapping("/teachers")
@AllArgsConstructor
public class TeacherControllers {

    private final TeacherServices teacherServices;

    /**
     * GET /teachers - Retrieves all active teachers with their college and courses
     * 
     * @return ResponseEntity with:
     *         - 200 OK: List of active teachers with full details
     *         - 204 No Content: If no active teachers found
     *         - 500 Internal Server Error: If unexpected error occurs
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TeacherEntityDto>>> getTeachers() {
        try {
            log.info("Fetching all active teachers");
            List<TeacherEntityDto> teachers = teacherServices.findAllTeachers();

            if (teachers.isEmpty()) {
                log.info("No active teachers found");
                return ResponseEntity.noContent().build();
            }

            log.info("Successfully fetched {} active teachers", teachers.size());
            return ResponseEntity.ok(
                    ApiResponse.ok(teachers, "Teachers fetched successfully"));
        } catch (Exception e) {
            log.error("Error fetching teachers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error("Failed to fetch teachers", "An error occurred while fetching teachers"));
        }
    }

    /**
     * POST /teachers - Creates a new teacher
     * 
     * @param request CreateTeacherRequestDto with teacher details
     * @return ResponseEntity with:
     *         - 201 Created: Newly created teacher with full details
     *         - 400 Bad Request: If validation fails or college not found
     *         - 500 Internal Server Error: If unexpected error occurs
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TeacherEntityDto>> createTeacher(
            @Valid @RequestBody CreateTeacherRequestDto request) {
        try {
            log.info("Creating new teacher: {}", request.getEmployeeId());
            TeacherEntityDto createdTeacher = teacherServices.createTeacher(request);

            log.info("Successfully created teacher with ID: {}", createdTeacher.getTeacherId());
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ApiResponse.ok(createdTeacher, "Teacher created successfully"));
        } catch (Exception e) {
            log.error("Error creating teacher", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error("Failed to create teacher", "An error occurred while creating teacher"));
        }
    }

    /**
     * GET /teachers/{id} - Get teacher by ID
     * 
     * @param id TeacherId to fetch
     * @return ResponseEntity with:
     *         - 200 OK: Teacher with full details
     *         - 404 Not Found: If teacher not found
     *         - 500 Internal Server Error: If unexpected error occurs
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TeacherEntityDto>> getTeacherById(@PathVariable long id) {
        try {
            log.info("Fetching teacher with ID: {}", id);
            TeacherEntityDto teacher = teacherServices.getTeacherById(id);

            log.info("Successfully fetched teacher with ID: {}", id);
            return ResponseEntity.ok(ApiResponse.ok(teacher, "Teacher fetched successfully"));
        } catch (Exception e) {
            log.error("Error fetching teacher with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error("Failed to fetch teacher", "An error occurred while fetching teacher"));
        }
    }

    /**
     * PUT /teachers/{id} - Update a teacher by ID
     * 
     * @param id      TeacherId to update
     * @param request UpdateTeacherRequestDto with updated teacher details
     * @return ResponseEntity with:
     *         - 200 OK: Updated teacher with full details
     *         - 404 Not Found: If teacher not found
     *         - 400 Bad Request: If validation fails
     *         - 500 Internal Server Error: If unexpected error occurs
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TeacherEntityDto>> updateTeacher(
            @PathVariable long id,
            @Valid @RequestBody UpdateTeacherRequestDto request) {
        try {
            log.info("Updating teacher with ID: {}", id);
            TeacherEntityDto updatedTeacher = teacherServices.updateTeacher(id, request);

            log.info("Successfully updated teacher with ID: {}", id);
            return ResponseEntity.ok(ApiResponse.ok(updatedTeacher, "Teacher updated successfully"));
        } catch (Exception e) {
            log.error("Error updating teacher with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error("Failed to update teacher", "An error occurred while updating teacher"));
        }
    }

    /**
     * DELETE /teachers/{id} - Delete a teacher by ID (soft delete - sets isActive
     * to false)
     * 
     * @param id TeacherId to delete
     * @return ResponseEntity with:
     *         - 200 OK: Deletion successful
     *         - 404 Not Found: If teacher not found
     *         - 500 Internal Server Error: If unexpected error occurs
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteTeacherById(@PathVariable long id) {
        try {
            log.info("Deleting teacher with ID: {}", id);
            teacherServices.deleteTeacherById(id);

            log.info("Successfully deleted teacher with ID: {}", id);
            return ResponseEntity.ok(ApiResponse.ok("", "Teacher deleted successfully"));
        } catch (Exception e) {
            log.error("Error deleting teacher with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error("Failed to delete teacher", "An error occurred while deleting teacher"));
        }
    }
}
