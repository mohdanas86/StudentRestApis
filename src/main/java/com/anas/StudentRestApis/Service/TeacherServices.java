package com.anas.StudentRestApis.Service;

import com.anas.StudentRestApis.Dto.TeacherEntityDto;

import java.util.List;

public interface TeacherServices {
    /**
     * Retrieves all active teachers with their courses and college information
     */
    List<TeacherEntityDto> findAllTeachers();
}
