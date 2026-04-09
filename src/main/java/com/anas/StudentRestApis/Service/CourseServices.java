package com.anas.StudentRestApis.Service;

import com.anas.StudentRestApis.Dto.CourseEntityDto;
import com.anas.StudentRestApis.Dto.CreateCourseRequestDto;
import com.anas.StudentRestApis.Dto.UpdateCourseRequestDto;

import java.util.List;

public interface CourseServices {
    /**
     * Retrieves all courses
     */
    List<CourseEntityDto> getAllCourses();

    /**
     * Retrieves all course by courseId
     */
    CourseEntityDto getCourseByCourseId(long courseId);

    /**
     * Create a new course
     */
    CourseEntityDto createCourse(CreateCourseRequestDto request);

    /**
     * Update a course using courseId
     */
    CourseEntityDto updateCourse(long courseId, UpdateCourseRequestDto request);

    /**
     * Delete a course using courseId
     */
    void deleteCourseByCourseId(long courseId);
}
