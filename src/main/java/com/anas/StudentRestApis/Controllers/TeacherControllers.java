package com.anas.StudentRestApis.Controllers;

import com.anas.StudentRestApis.Entity.TeacherEntity;
import com.anas.StudentRestApis.Repository.TeacherRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/teachers")
@AllArgsConstructor
@Tag(name="Teacher Management", description = "Endpoints for managing faculty and teacher records")
public class TeacherControllers {
    private final TeacherRepository teacherRepository;

    @GetMapping
    @Operation(
            summary = "Get all teachers",
            description = "Retrieves a complete list of all teachers along with their assigned courses."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of teachers"),
            @ApiResponse(responseCode = "204", description = "No teachers found in the database"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<TeacherEntity>> getTeachers(){
        List<TeacherEntity> teachers = teacherRepository.findAll();

        if (teachers.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 if no data
        }

        return ResponseEntity.ok(teachers);
    }
}
