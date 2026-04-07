package com.anas.StudentRestApis.Service;

import com.anas.StudentRestApis.Dto.CollegeEntityDto;

import java.util.List;

public interface CollegeServices {
    /**
     * Retrieves all colleges
     */
    List<CollegeEntityDto> getAllColleges();
}
