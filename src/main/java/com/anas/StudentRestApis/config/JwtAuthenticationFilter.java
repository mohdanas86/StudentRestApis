package com.anas.StudentRestApis.config;

import com.anas.StudentRestApis.Entity.UserEntity;
import com.anas.StudentRestApis.Repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

/**
 * JwtAuthenticationFilter - Extract and validate JWT tokens from requests
 *
 * Extends OncePerRequestFilter to ensure this filter runs only once per request
 *
 * Process:
 * 1. Extract JWT from Authorization header
 * 2. Validate token signature and expiration
 * 3. Load user from database
 * 4. Create Spring Authentication object
 * 5. Store in SecurityContext for controller access
 *
 * If any step fails, request continues without authentication
 * Spring Security will later reject unauthorized requests
 */
@Slf4j
@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    // ==================== Filter Execution ====================
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            // extract jwt from request
            String jwt = extractJwtFromRequest(request);

            // if jwt exists and is void, authenticate the user
            if (jwt != null && jwtTokenProvider.validateToken(jwt)) {
                authenticateUserFromToken(jwt, request);
                log.debug("User authenticated successfully from JWT token");
            }

        } catch (Exception ex) {
            log.warn("Could not authenticate user from JWT token: {}", ex.getMessage());
            // Continue without authentication - Spring Security will handle authorization
        }

        // continue filter chain
        filterChain.doFilter(request, response);
    }

    // ==================== Supporting Methods ====================

    /**
     * Extract JWT token from request
     *
     * Expected format:
     * Authorization: Bearer <token>
     *
     * @param request HTTP request
     * @return JWT token or null if not found
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove "Bearer " prefix
        }

        return null;
    }

    /**
     * Authenticate user from valid JWT token
     *
     * Steps:
     * 1. Extract username from JWT
     * 2. Load user from database
     * 3. Check user is active
     * 4. Create Authentication object with authorities
     * 5. Store in SecurityContext
     *
     * @param jwt     Valid JWT token
     * @param request HTTP request
     */
    private void authenticateUserFromToken(String jwt, HttpServletRequest request) {
        // extract username from token
        String username = jwtTokenProvider.getUsernameFromToken(jwt);

        // load user form database
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found;"));

        // verify user if active
        if (!user.isActive()) {
            throw new RuntimeException("User account is deactivated");
        }

        // get role from token
        String roleFromToken = jwtTokenProvider.getRoleFromToken(jwt);

        // create authorities collection
        Collection<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole().getAuthority()));

        // create authentication token
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                username, // principle (username)
                null, // credentials (null - already authenticated)
                authorities // authorities (roles)
        );

        // attach request details
        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request));

        // store is securityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug("User {} authenticated with role {}", username, roleFromToken);
    }

    /**
     * Determine if this request should be filtered
     * Skip JWT filter for public endpoints
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        // public request that don't need jwt
        // Note: Context path (/api/v1) is already stripped by the request URI
        return path.startsWith("/auth/") ||
                path.startsWith("/health") ||
                path.startsWith("/swagger") ||
                path.startsWith("/api-docs");
    }
}
