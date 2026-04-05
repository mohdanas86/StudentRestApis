package com.anas.StudentRestApis.config;

import com.anas.StudentRestApis.Dto.TeacherEntityDto;
import com.anas.StudentRestApis.Entity.TeacherEntity;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ModelMapperConfig - Configures ModelMapper for Entity-DTO mapping
 */
@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        // Configure TeacherEntity -> TeacherEntityDto mapping
        // Skip college-related fields (manually set in service)
        mapper.addMappings(new PropertyMap<TeacherEntity, TeacherEntityDto>() {
            @Override
            protected void configure() {
                skip().setCollegeId(null);
                skip().setCollegeName(null);
            }
        });

        return mapper;
    }
}
