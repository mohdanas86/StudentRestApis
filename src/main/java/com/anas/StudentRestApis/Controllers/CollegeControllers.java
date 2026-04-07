package com.anas.StudentRestApis.Controllers;

import com.anas.StudentRestApis.Common.ApiResponse;
import com.anas.StudentRestApis.Dto.CollegeEntityDto;
import com.anas.StudentRestApis.Service.CollegeServices;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CollegeControllers - REST endpoints for college operations
 */
@Slf4j
@RestController
@RequestMapping("/colleges")
@AllArgsConstructor
public class CollegeControllers {

    private final CollegeServices collegeServices;

    /**
     * GET /colleges - Retrieves all colleges
     *
     * @return ResponseEntity with:
     *         - 200 OK: List of colleges with full details
     *         - 204 No Content: If no colleges found
     *         - 500 Internal Server Error: If unexpected error occurs
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CollegeEntityDto>>> getAllColleges() {
        try {
            log.info("Fetching all colleges");
            List<CollegeEntityDto> colleges = collegeServices.getAllColleges();

            if (colleges.isEmpty()) {
                log.info("No colleges found");
                return ResponseEntity.noContent().build();
            }

            log.info("Successfully fetched {} colleges", colleges.size());
            return ResponseEntity.ok(
                    ApiResponse.ok(colleges, "Colleges fetched successfully"));
        } catch (Exception e) {
            log.error("Error fetching colleges", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error("Failed to fetch colleges", e.getMessage()));
        }
    }
}
