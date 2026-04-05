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

@Service
@AllArgsConstructor
public class TeacherServicesImpl implements TeacherServices {

    private final TeacherRepository teacherRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TeacherEntityDto> findAllTeachers() {
        return teacherRepository.findAll()
                .stream()
                // Filter only active teachers
                .filter(teacher -> Boolean.TRUE.equals(teacher.getIsActive()))
                .map(teacherEntity -> {
                    // 1. Basic Mapping (IDs, names, email, specialization)
                    TeacherEntityDto dto = modelMapper.map(teacherEntity, TeacherEntityDto.class);

                    // 2. Map the Single College Name (from the direct relationship)
                    if (teacherEntity.getCollege() != null) {
                        dto.setCollegeName(teacherEntity.getCollege().getCollegeName());
                    }

                    // 3. Map Course Names (from the Many-to-Many relationship)
                    if (teacherEntity.getCourses() != null && !teacherEntity.getCourses().isEmpty()) {
                        List<String> names = teacherEntity.getCourses().stream()
                                .map(CourseEntity::getCourseName)
                                .toList();
                        dto.setCourseNames(names);
                    }

                    return dto;
                })
                .toList();
    }
}