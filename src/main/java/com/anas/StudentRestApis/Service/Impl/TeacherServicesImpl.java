package com.anas.StudentRestApis.Service.Impl;

import com.anas.StudentRestApis.Dto.CreateTeacherRequestDto;
import com.anas.StudentRestApis.Dto.TeacherEntityDto;
import com.anas.StudentRestApis.Dto.UpdateTeacherRequestDto;
import com.anas.StudentRestApis.Entity.CourseEntity;
import com.anas.StudentRestApis.Entity.TeacherEntity;
import com.anas.StudentRestApis.Exception.ResourceNotFoundException;
import com.anas.StudentRestApis.Repository.CollegeRepository;
import com.anas.StudentRestApis.Repository.TeacherRepository;
import com.anas.StudentRestApis.Service.TeacherServices;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for teacher data operations
 */
@Slf4j
@Service
@AllArgsConstructor
public class TeacherServicesImpl implements TeacherServices {

    private final TeacherRepository teacherRepository;
    private final CollegeRepository collegeRepository;
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

    /**
     * Create a new teacher
     */
    @Override
    @Transactional
    public TeacherEntityDto createTeacher(CreateTeacherRequestDto request) {

        // Fetch college by ID, throw 404 if not found
        var college = collegeRepository.findById(request.getCollegeId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("College not found with ID: " + request.getCollegeId()));

        // Create and save new teacher
        TeacherEntity newTeacher = TeacherEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .employeeId(request.getEmployeeId())
                .specialization(request.getSpecialization())
                .college(college)
                .isActive(true)
                .build();

        TeacherEntity savedTeacher = teacherRepository.save(newTeacher);

        // Map to DTO
        TeacherEntityDto dto = modelMapper.map(savedTeacher, TeacherEntityDto.class);
        dto.setCollegeId(college.getCollegeId());
        dto.setCollegeName(college.getCollegeName());
        dto.setCourseNames(List.of());

        return dto;
    }

    /**
     * Get teacher by ID with college and course details
     */
    @Override
    @Transactional(readOnly = true)
    public TeacherEntityDto getTeacherById(long id) {
        TeacherEntity teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with ID: " + id));

        // Map to DTO with full details
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
    }

    /**
     * Update teacher by ID
     */
    @Override
    @Transactional
    public TeacherEntityDto updateTeacher(long id, UpdateTeacherRequestDto request) {
        log.info("Fetch teacher with id: " + id);
        // Fetch teacher by ID, throw 404 if not found
        TeacherEntity teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with ID: " + id));

        // Update fields if provided
        if (request.getFirstName() != null) {
            teacher.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            teacher.setLastName(request.getLastName());
        }
        if (request.getEmail() != null) {
            teacher.setEmail(request.getEmail());
        }
        if (request.getEmployeeId() != null) {
            teacher.setEmployeeId(request.getEmployeeId());
        }
        if (request.getSpecialization() != null) {
            teacher.setSpecialization(request.getSpecialization());
        }
        // Only update isActive if explicitly provided (not null)
        if (request.getIsActive() != null) {
            teacher.setIsActive(request.getIsActive());
        }

        // Save updated teacher
        TeacherEntity updatedTeacher = teacherRepository.save(teacher);

        // Map to DTO
        TeacherEntityDto dto = modelMapper.map(updatedTeacher, TeacherEntityDto.class);

        if (updatedTeacher.getCollege() != null) {
            dto.setCollegeId(updatedTeacher.getCollege().getCollegeId());
            dto.setCollegeName(updatedTeacher.getCollege().getCollegeName());
        }

        if (updatedTeacher.getCourses() != null && !updatedTeacher.getCourses().isEmpty()) {
            dto.setCourseNames(updatedTeacher.getCourses().stream()
                    .map(CourseEntity::getCourseName)
                    .toList());
        } else {
            dto.setCourseNames(List.of());
        }

        return dto;
    }

    /**
     * Delete teacher by ID (soft delete - sets isActive to false)
     */
    @Override
    @Transactional
    public void deleteTeacherById(long id) {
        TeacherEntity teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with ID: " + id));

        // Soft delete - mark as inactive
        teacher.setIsActive(false);
        teacherRepository.save(teacher);
    }
}