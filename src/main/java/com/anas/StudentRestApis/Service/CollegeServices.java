package com.anas.StudentRestApis.Service;

import com.anas.StudentRestApis.Dto.CollegeEntityDto;
import com.anas.StudentRestApis.Dto.CreateCollegeRequestDto;
import com.anas.StudentRestApis.Dto.UpdateCollegeRequestDto;

import java.util.List;

public interface CollegeServices {
    /**
     * Retrieves all colleges
     */
    List<CollegeEntityDto> getAllColleges();

    /**
     * Retrieve college by collegeId
     */
    CollegeEntityDto getCollegeByCollegeId(long collegeId);

    /**
     * Create new college
     */
    CollegeEntityDto createCollege(CreateCollegeRequestDto request);

    /**
     * Update a college by collegeId
     */
    CollegeEntityDto updateCollegeByCollegeId(long collegeId, UpdateCollegeRequestDto request);

    /**
     * Delete a college by collegeId
     */
    void deleteCollegeByCollegeId(long collegeId);

}
