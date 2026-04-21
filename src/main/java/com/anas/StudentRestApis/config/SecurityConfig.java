package com.anas.StudentRestApis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * SecurityConfig - Spring Security configuration
 *
 * Configuration Strategy:
 * 1. Disable CSRF (stateless REST API using JWT)
 * 2. Use stateless session (JWT-based, no HttpSession)
 * 3. Configure URL authorization rules
 * 4. Add JWT filter before default authentication filter
 * 5. Configure CORS for browser-based clients
 * 6. Use BCrypt for password hashing
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, // @PreAuthorize, @PostAuthorize
                securedEnabled = true // @Secured
)
public class SecurityConfig {
        @Bean
        public MethodSecurityExpressionHandler methodSecurityExpressionHandler(){
                return new DefaultMethodSecurityExpressionHandler();
        }

        // ==================== Security Filter Chain ====================

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http,
                        CorsConfigurationSource corsConfigurationSource,
                        JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
                http
                                // ==================== CSRF ====================
                                // Disable CSRF for stateless REST API
                                // CSRF tokens are for session-based auth (form submissions)
                                .csrf(csrf -> csrf.disable())
                                // ==================== CORS ====================
                                // Enable CORS handling
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                // ==================== Session Management ====================
                                // Use JWT - no server-side sessions
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                // ==================== Authorization ====================
                                .authorizeHttpRequests(auth -> auth
                                                // public endpoints
                                                .requestMatchers(
                                                                "/auth/login",
                                                                "/auth/register",
                                                                "/auth/refresh",
                                                                "/auth/validate",
                                                                "/health",
                                                                "/health/**",
                                                                "/swagger-ui/**",
                                                                "/v3/api-docs/**",
                                                                "/")
                                                .permitAll()
                                                // Admin-only endpoints
                                                .requestMatchers("/admin/**")
                                                .hasRole("ADMIN")

                                                // Teacher endpoints
                                                .requestMatchers(HttpMethod.POST, "/courses/**")
                                                .hasAnyRole("ADMIN", "TEACHER")

                                                .requestMatchers(HttpMethod.PUT, "/courses/**")
                                                .hasAnyRole("ADMIN", "TEACHER")

                                                // All other requests require authentication
                                                .anyRequest().authenticated())
                                // ==================== Authentication Filters ====================
                                // Add JWT filter before default authentication filter
                                // This allows JWT validation before default Spring Security checks
                                .addFilterBefore(
                                                jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class)
                                // Add to securityFilterChain method
                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(new EntryPointUnauthorizedHandler()))
                                // ==================== Disable Default Auth Mechanisms ====================
                                .httpBasic(basic -> basic.disable())
                                .formLogin(form -> form.disable());

                return http.build();

        }

        // ==================== CORS Configuration ====================

        /**
         * Configure CORS for browser-based clients
         *
         * CORS allows requests from different domains
         * Important for web frontends calling this API
         */
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();

                // allowed origins
                configuration.setAllowedOrigins(Arrays.asList(
                                "http://localhost:3000",
                                "http://localhost:8080",
                                "https://yourdomain.com"));

                // allowed http methods
                configuration.setAllowedMethods(Arrays.asList(
                                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

                // allowed request header
                configuration.setAllowedHeaders(Arrays.asList(
                                "Authorization",
                                "Content-Disposition",
                                "X-Total-Count"));

                // Whether to allow credentials (cookies, authorization headers)
                configuration.setAllowCredentials(false);

                // Cache preflight response for 1 hour
                configuration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/api/**", configuration);

                return source;
        }
}
