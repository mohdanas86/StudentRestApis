package com.anas.StudentRestApis.Controllers;

import com.anas.StudentRestApis.Common.ApiResponse;
import com.anas.StudentRestApis.Dto.TeacherEntityDto;
import com.anas.StudentRestApis.Service.TeacherServices;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
                    ApiResponse.error("Failed to fetch teachers", e.getMessage()));
        }
    }
}
