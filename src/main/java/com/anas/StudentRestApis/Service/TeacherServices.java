package com.anas.StudentRestApis.Service;

import com.anas.StudentRestApis.Dto.CreateTeacherRequestDto;
import com.anas.StudentRestApis.Dto.TeacherEntityDto;
import com.anas.StudentRestApis.Dto.UpdateTeacherRequestDto;

import java.util.List;

public interface TeacherServices {
    /**
     * Retrieves all active teachers with their courses and college information
     */
    List<TeacherEntityDto> findAllTeachers();

    /**
     * Create a teacher
     */
    TeacherEntityDto createTeacher(CreateTeacherRequestDto createTeacherRequestDto);

    /**
     * Get a teacher by ID
     */
    TeacherEntityDto getTeacherById(long id);

    /**
     * Update a teacher by teacherId
     */
    TeacherEntityDto updateTeacher(long id, UpdateTeacherRequestDto updateTeacherRequestDto);

    /**
     * Delete a teacher by teacherId
     */
    void deleteTeacherById(long id);
}
