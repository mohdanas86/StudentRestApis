package com.anas.StudentRestApis.Controllers;

import com.anas.StudentRestApis.Common.ApiResponse;
import com.anas.StudentRestApis.Dto.CollegeEntityDto;
import com.anas.StudentRestApis.Exception.ResourceNotFoundException;
import com.anas.StudentRestApis.Service.CollegeServices;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     *         - 204 No Content: If no college found
     *         - 500 Internal Server Error: If unexpected error occurs
     */
    @GetMapping
    public ApiResponse<CollegeEntityDto> getAllColleges(){
        try{
            log.info("Fetching colleges");
            List<CollegeEntityDto> colleges = collegeServices.getAllColleges();

            if(colleges.isEmpty()){
                throw new ResourceNotFoundException("Colleges not found");
            }

            log.info("Successfully fetched {} active teachers", colleges.size());
            return ApiResponse.ok((CollegeEntityDto) colleges, "Teachers fetched successfully");
        }catch(Exception e){
            log.info("Error fetching colleges", e);
            return ApiResponse.error("Failed to fetch colleges", e.getMessage());
//            esponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
        }
    }
}
