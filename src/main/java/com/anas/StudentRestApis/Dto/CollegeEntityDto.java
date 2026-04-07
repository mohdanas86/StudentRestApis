package com.anas.StudentRestApis.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for CollegeEntity - Contains college information for API responses
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CollegeEntityDto {

    private long collegeId;
    private String collegeCode;
    private String collegeName;
    private String deanName;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;


}
