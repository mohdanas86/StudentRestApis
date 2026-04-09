package com.anas.StudentRestApis.Service.Impl;

import com.anas.StudentRestApis.Dto.CourseEntityDto;
import com.anas.StudentRestApis.Dto.CreateCourseRequestDto;
import com.anas.StudentRestApis.Dto.UpdateCourseRequestDto;
import com.anas.StudentRestApis.Entity.CollegeEntity;
import com.anas.StudentRestApis.Entity.CourseEntity;
import com.anas.StudentRestApis.Exception.ResourceNotFoundException;
import com.anas.StudentRestApis.Repository.CollegeRepository;
import com.anas.StudentRestApis.Repository.CourseRepository;
import com.anas.StudentRestApis.Service.CourseServices;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for courses data operations
 */
@Service
@AllArgsConstructor
public class CourseServicesImpl implements CourseServices {

    private final CourseRepository courseRepository;
    private final CollegeRepository collegeRepository;
    private final ModelMapper modelMapper;

    /**
     * Fetch all courses
     */
    @Override
    @Transactional
    public List<CourseEntityDto> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(courseEntity -> modelMapper.map(courseEntity, CourseEntityDto.class))
                .toList();
    }

    /**
     * Fetch course by courseId
     */
    @Override
    @Transactional
    public CourseEntityDto getCourseByCourseId(long courseId) {
        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        return modelMapper.map(course, CourseEntityDto.class);
    }

    /**
     * Create a new course
     */
    @Override
    @Transactional
    public CourseEntityDto createCourse(CreateCourseRequestDto request) {
        CollegeEntity college = collegeRepository.findById(request.getCollegeId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("College not found with id: " + request.getCollegeId()));

        CourseEntity course = CourseEntity.builder()
                .courseCode(request.getCourseCode())
                .courseName(request.getCourseName())
                .credits(request.getCredits())
                .college(college)
                .build();

        CourseEntity createCourse = courseRepository.save(course);

        return modelMapper.map(createCourse, CourseEntityDto.class);
    }

    /**
     * Update a course using courseId
     */
    @Override
    @Transactional
    public CourseEntityDto updateCourse(long courseId, UpdateCourseRequestDto request) {
        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        if (request.getCourseCode() != null) {
            course.setCourseCode(request.getCourseCode());
        }
        if (request.getCourseName() != null) {
            course.setCourseName(request.getCourseName());
        }
        if (request.getCredits() != course.getCredits() && request.getCredits() > 0.0) {
            course.setCredits(request.getCredits());
        }
        if (request.getCollegeId() != null) {
            CollegeEntity college = collegeRepository.findById(request.getCollegeId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "College not found with id: " + request.getCollegeId()));
            course.setCollege(college);
        }

        CourseEntity updateCourse = courseRepository.save(course);

        return modelMapper.map(updateCourse, CourseEntityDto.class);
    }

    /**
     * Delete a course using courseId
     */
    @Override
    @Transactional
    public void deleteCourseByCourseId(long courseId) {
        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        courseRepository.deleteById(courseId);
    }
}
