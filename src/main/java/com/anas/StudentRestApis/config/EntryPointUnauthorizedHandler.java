package com.anas.StudentRestApis.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import org.springframework.security.core.AuthenticationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * EntryPointUnauthorizedHandler - Handles unauthenticated requests to protected endpoints
 *
 * Called when:
 * 1. Request lacks JWT token
 * 2. Request has invalid JWT token
 * 3. User tries to access protected endpoint without authentication
 *
 * Returns JSON error response instead of HTML login page
 */
@Slf4j
@Component
public class EntryPointUnauthorizedHandler implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        log.warn("Unauthorized access attempt to: {} - {}",
                request.getRequestURI(), authException.getMessage());

        // Set response content type
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Build error response
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("success", false);
        errorDetails.put("message", "Unauthorized");
        errorDetails.put("error", authException.getMessage());
        errorDetails.put("path", request.getRequestURI());
        errorDetails.put("timestamp", System.currentTimeMillis());

        // Write JSON response
        response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
    }
}
