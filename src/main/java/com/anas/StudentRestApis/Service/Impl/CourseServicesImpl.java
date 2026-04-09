package com.anas.StudentRestApis.Service.Impl;

import com.anas.StudentRestApis.Dto.CourseEntityDto;
import com.anas.StudentRestApis.Entity.CourseEntity;
import com.anas.StudentRestApis.Repository.CourseRepository;
import com.anas.StudentRestApis.Service.CourseServices;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for courses data operations
 */
@Service
@AllArgsConstructor
public class CourseServicesImpl implements CourseServices{

    private final CourseRepository courseRepository;
    private final ModelMapper modelMapper;

    /**
     * Fetch all courses
     */
    @Override
    public List<CourseEntityDto> getAllCourses(){
        return courseRepository.findAll()
                .stream()
                .map(courseEntity -> modelMapper.map(courseEntity, CourseEntityDto.class))
                .toList();
    }
}
