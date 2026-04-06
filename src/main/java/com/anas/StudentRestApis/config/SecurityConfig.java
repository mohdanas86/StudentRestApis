package com.anas.StudentRestApis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig - Spring Security configuration for REST API
 * CSRF: Disabled (stateless REST API)
 * Auth: All routes permitted (development mode - no authentication required)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

        /**
         * Allow all routes - No authentication required for development
         */
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                                .httpBasic(basic -> basic.disable())
                                .formLogin(form -> form.disable());

                return http.build();
        }
}
