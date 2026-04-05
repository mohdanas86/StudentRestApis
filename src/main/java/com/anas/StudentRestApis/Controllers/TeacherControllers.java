package com.anas.StudentRestApis.Controllers;

import com.anas.StudentRestApis.Common.ApiResponse;
import com.anas.StudentRestApis.Dto.TeacherEntityDto;
import com.anas.StudentRestApis.Service.TeacherServices;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * TeacherControllers - REST endpoints for teacher operations
 */
@RestController
@RequestMapping("/teachers")
@AllArgsConstructor
public class TeacherControllers {

    private final TeacherServices teacherServices;

    /**
     * GET /teachers - Retrieves all active teachers
     * 
     * @return 200 OK with teachers list, 204 No Content if none found
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TeacherEntityDto>>> getTeachers() {
        try {
            List<TeacherEntityDto> teachers = teacherServices.findAllTeachers();

            if (teachers.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(
                    ApiResponse.ok(teachers, "Teachers fetched successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error("Failed to fetch teachers", e.getMessage()));
        }
    }
}
