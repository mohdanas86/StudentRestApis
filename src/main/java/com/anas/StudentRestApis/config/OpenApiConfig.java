package com.anas.StudentRestApis.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI restTeacherApi() {
        // Define the Security Scheme (JWT Bearer)
        SecurityScheme securityScheme = new SecurityScheme()
                .name("Bearer Authentication")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        // Define the Security Requirement
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("Bearer Authentication");

        return new OpenAPI()
                .info(new Info()
                        .title("SRMIST Student-Teacher Management API")
                        .version("1.0.0")
                        .description("API Documentation for managing Colleges, Courses, and Teachers.")
                        .contact(new Contact()
                                .name("Anas Alam")
                                .email("anas.alam@srm.edu")))
                .addSecurityItem(securityRequirement)
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", securityScheme));
    }
}
