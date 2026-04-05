package com.anas.StudentRestApis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig - Spring Security configuration for REST API
 * CSRF: Disabled (stateless REST API)
 * Auth: HTTP Basic Auth required
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                                .httpBasic(basic -> {
                                })
                                .formLogin(form -> form.disable());

                return http.build();
        }
}
