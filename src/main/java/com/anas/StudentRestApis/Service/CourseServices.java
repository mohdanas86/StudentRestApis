package com.anas.StudentRestApis.Service;

import com.anas.StudentRestApis.Dto.CourseEntityDto;

import java.util.List;

public interface CourseServices {
    /**
     * Retrieves all courses
     */
    List<CourseEntityDto> getAllCourses();
}
