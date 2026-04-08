package com.anas.StudentRestApis.Service.Impl;

import com.anas.StudentRestApis.Dto.CollegeEntityDto;
import com.anas.StudentRestApis.Dto.CreateCollegeRequestDto;
import com.anas.StudentRestApis.Dto.UpdateCollegeRequestDto;
import com.anas.StudentRestApis.Entity.CollegeEntity;
import com.anas.StudentRestApis.Exception.DuplicateResourceException;
import com.anas.StudentRestApis.Exception.ResourceNotFoundException;
import com.anas.StudentRestApis.Repository.CollegeRepository;
import com.anas.StudentRestApis.Service.CollegeServices;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for college data operations
 */
@Slf4j
@Service
@AllArgsConstructor
public class CollegeServicesImpl implements CollegeServices {

    private final CollegeRepository collegeRepository;
    private final ModelMapper modelMapper;

    /**
     * Fetch all colleges
     */
    @Override
    public List<CollegeEntityDto> getAllColleges() {
        return collegeRepository.findAll()
                .stream()
                .map(college -> modelMapper.map(college, CollegeEntityDto.class))
                .toList();
    }

    /**
     * Fetch college by collegeId
     */
    @Override
    @Transactional
    public CollegeEntityDto getCollegeByCollegeId(long collegeId) {
        CollegeEntity college = collegeRepository.findById(collegeId)
                .orElseThrow(() -> new ResourceNotFoundException("College not found with ID: " + collegeId));

        return modelMapper.map(college, CollegeEntityDto.class);
    }

    /**
     * Create new college
     */
    @Override
    @Transactional
    public CollegeEntityDto createCollege(CreateCollegeRequestDto request) {
        try {
            CollegeEntity college = CollegeEntity.builder()
                    .collegeCode(request.getCollegeCode())
                    .collegeName(request.getCollegeName())
                    .deanName(request.getDeanName())
                    .build();

            collegeRepository.save(college);

            return modelMapper.map(college, CollegeEntityDto.class);
        } catch (DataIntegrityViolationException e) {
            log.warn("Duplicate college code: {}", request.getCollegeCode());
            throw new DuplicateResourceException("College with code '" + request.getCollegeCode() + "' already exists",
                    e);
        }
    }

    /**
     * Update a college by collegeId
     */
    @Override
    @Transactional
    public CollegeEntityDto updateCollegeByCollegeId(long collegeId, UpdateCollegeRequestDto request) {
        try {
            CollegeEntity college = collegeRepository.findById(collegeId)
                    .orElseThrow(() -> new ResourceNotFoundException("College not found with ID: " + collegeId));

            if (request.getCollegeCode() != null) {
                college.setCollegeCode(request.getCollegeCode());
            }

            if (request.getCollegeName() != null) {
                college.setCollegeName(request.getCollegeName());
            }

            if (request.getDeanName() != null) {
                college.setDeanName(request.getDeanName());
            }

            CollegeEntity updatedCollege = collegeRepository.save(college);

            return modelMapper.map(updatedCollege, CollegeEntityDto.class);
        } catch (DataIntegrityViolationException e) {
            log.warn("Duplicate college code during update: {}", request.getCollegeCode());
            throw new DuplicateResourceException("College with code '" + request.getCollegeCode() + "' already exists",
                    e);
        }
    }

    /**
     * Delete a college by collegeId
     */
    @Override
    @Transactional
    public void deleteCollegeByCollegeId(long collegeId) {
        CollegeEntity college = collegeRepository.findById(collegeId)
                .orElseThrow(() -> new ResourceNotFoundException("College not found with ID: " + collegeId));

        collegeRepository.deleteById(collegeId);
    }
}
