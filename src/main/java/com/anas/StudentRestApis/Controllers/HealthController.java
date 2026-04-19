package com.anas.StudentRestApis.Controllers;

import com.anas.StudentRestApis.Common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * HealthController - Simple health check endpoint
 *
 * Used by:
 * - Load balancers to verify service is running
 * - Kubernetes liveness probes
 * - Monitoring systems
 *
 * No authentication required
 */
@RestController
@RequestMapping("/health")
public class HealthController {
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> health(){
        Map<String, Object> healthData = new HashMap<>();
        healthData.put("status", "UP");
        healthData.put("application", "StudentRestAPIs");
        healthData.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(
                ApiResponse.ok(healthData, "Service is healthy")
        );
    }
}
