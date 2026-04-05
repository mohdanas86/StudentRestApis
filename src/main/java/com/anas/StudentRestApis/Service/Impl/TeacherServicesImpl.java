package com.anas.StudentRestApis.Service.Impl;

import com.anas.StudentRestApis.Dto.TeacherEntityDto;
import com.anas.StudentRestApis.Entity.CourseEntity;
import com.anas.StudentRestApis.Repository.TeacherRepository;
import com.anas.StudentRestApis.Service.TeacherServices;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for teacher data operations
 */
@Service
@AllArgsConstructor
public class TeacherServicesImpl implements TeacherServices {

    private final TeacherRepository teacherRepository;
    private final ModelMapper modelMapper;

    /**
     * Fetch all active teachers with college and course details
     */
    @Override
    @Transactional(readOnly = true)
    public List<TeacherEntityDto> findAllTeachers() {
        return teacherRepository.findAll()
                .stream()
                .filter(teacher -> Boolean.TRUE.equals(teacher.getIsActive()))
                .map(teacher -> {
                    TeacherEntityDto dto = modelMapper.map(teacher, TeacherEntityDto.class);

                    if (teacher.getCollege() != null) {
                        dto.setCollegeId(teacher.getCollege().getCollegeId());
                        dto.setCollegeName(teacher.getCollege().getCollegeName());
                    }

                    if (teacher.getCourses() != null && !teacher.getCourses().isEmpty()) {
                        dto.setCourseNames(teacher.getCourses().stream()
                                .map(CourseEntity::getCourseName)
                                .toList());
                    } else {
                        dto.setCourseNames(List.of());
                    }

                    return dto;
                })
                .toList();
    }
}