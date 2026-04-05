package com.anas.StudentRestApis.Service.Impl;

import com.anas.StudentRestApis.Dto.TeacherEntityDto;
import com.anas.StudentRestApis.Entity.CourseEntity;
import com.anas.StudentRestApis.Repository.TeacherRepository;
import com.anas.StudentRestApis.Service.TeacherServices;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of TeacherServices - Handles teacher business logic
 */
@Service
@AllArgsConstructor
public class TeacherServicesImpl implements TeacherServices {

    private final TeacherRepository teacherRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<TeacherEntityDto> findAllTeachers() {
        return teacherRepository.findAll()
                .stream()
                .filter(teacher -> teacher.getIsActive() != null && teacher.getIsActive())
                .map(teacherEntity -> {
                    TeacherEntityDto dto = modelMapper.map(teacherEntity, TeacherEntityDto.class);

                    if (teacherEntity.getCourses() != null && !teacherEntity.getCourses().isEmpty()) {
                        // Extract course names
                        List<String> courseNames = teacherEntity.getCourses().stream()
                                .map(CourseEntity::getCourseName)
                                .toList();
                        dto.setCourseNames(courseNames);

                        // Extract unique colleges where teacher teaches (sorted)
                        List<String> collegeNames = teacherEntity.getCourses().stream()
                                .map(course -> course.getCollege().getCollegeName())
                                .collect(Collectors.toCollection(HashSet::new))
                                .stream()
                                .sorted()
                                .toList();
                        dto.setCollegeNames(collegeNames);
                    }

                    return dto;
                })
                .toList();
    }
}