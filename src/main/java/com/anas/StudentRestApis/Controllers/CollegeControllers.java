package com.anas.StudentRestApis.Controllers;

import com.anas.StudentRestApis.Common.ApiResponse;
import com.anas.StudentRestApis.Dto.CollegeEntityDto;
import com.anas.StudentRestApis.Dto.CreateCollegeRequestDto;
import com.anas.StudentRestApis.Dto.UpdateCollegeRequestDto;
import com.anas.StudentRestApis.Service.CollegeServices;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("permitAll()")
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
                    ApiResponse.error("Failed to fetch colleges", "An error occurred while fetching colleges"));
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
    @GetMapping("/{collegeId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<CollegeEntityDto>> getCollegeById(@PathVariable long collegeId) {
        try {
            log.info("Fetching college by ID: {}", collegeId);
            CollegeEntityDto college = collegeServices.getCollegeByCollegeId(collegeId);

            log.info("Successfully fetched collegeId: {}", college.getCollegeId());
            return ResponseEntity.ok(
                    ApiResponse.ok(college, "College fetched successfully"));
        } catch (Exception e) {
            log.error("Error fetching college", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error("Failed to fetch college", "An error occurred while fetching college"));
        }
    }

    /**
     * POST /colleges - Create new college
     *
     * @return ResponseEntity with:
     *         - 201 Created: college with full details
     *         - 400 Bad Request: If validation fails or duplicate college code
     *         - 500 Internal Server Error: If unexpected error occurs
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CollegeEntityDto>> createCollege(
            @Valid @RequestBody CreateCollegeRequestDto request) {
        try {
            log.info("Creating new college with code: {}", request.getCollegeCode());
            CollegeEntityDto college = collegeServices.createCollege(request);

            log.info("College created successfully: {}", college.getCollegeId());
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ApiResponse.ok(college, "College created successfully"));
        } catch (Exception e) {
            log.error("Error creating college", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error("Failed to create college", "An error occurred while creating college"));
        }
    }

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
    @PutMapping("/{collegeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CollegeEntityDto>> updateCollegeById(
            @PathVariable long collegeId,
            @Valid @RequestBody UpdateCollegeRequestDto request) {
        try {
            log.info("Updating college: {}", collegeId);
            CollegeEntityDto college = collegeServices.updateCollegeByCollegeId(collegeId, request);

            log.info("College updated successfully: {}", college.getCollegeId());
            return ResponseEntity.ok(
                    ApiResponse.ok(college, "College updated successfully"));
        } catch (Exception e) {
            log.error("Error updating college", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error("Failed to update college", "An error occurred while updating college"));
        }
    }

    /**
     * DELETE /colleges/{collegeId} - Delete a college by ID
     *
     * @param collegeId CollegeId to delete
     * @return ResponseEntity with:
     *         - 200 OK: Deletion successful
     *         - 404 Not Found: If college not found
     *         - 500 Internal Server Error: If unexpected error occurs
     */
    @DeleteMapping("/{collegeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteCollege(@PathVariable long collegeId) {
        try {
            log.info("Deleting college: {}", collegeId);
            collegeServices.deleteCollegeByCollegeId(collegeId);
            return ResponseEntity.ok(
                    ApiResponse.ok(null, "College deleted successfully"));
        } catch (Exception e) {
            log.error("Error deleting college", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error("Failed to delete college", "An error occurred while deleting college"));
        }
    }
}