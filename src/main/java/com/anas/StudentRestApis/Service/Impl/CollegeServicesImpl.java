package com.anas.StudentRestApis.Service.Impl;

import com.anas.StudentRestApis.Dto.CollegeEntityDto;
import com.anas.StudentRestApis.Repository.CollegeRepository;
import com.anas.StudentRestApis.Service.CollegeServices;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for college data operations
 */
@Service
@AllArgsConstructor
public class CollegeServicesImpl implements CollegeServices {

    private final CollegeRepository collegeRepository;
    private final ModelMapper modelMapper;

    /**
     * Fetch all colleges
     */
    @Override
    public List<CollegeEntityDto> getAllColleges(){
        return collegeRepository.findAll()
                .stream()
                .map(college -> modelMapper.map(college, CollegeEntityDto.class))
                .toList();
    }
}
