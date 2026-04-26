package com.anas.StudentRestApis.config;

import com.anas.StudentRestApis.Annotation.Auditable;
import com.anas.StudentRestApis.Entity.AuditAction;
import com.anas.StudentRestApis.Entity.AuditStatus;
import com.anas.StudentRestApis.Entity.UserEntity;
import com.anas.StudentRestApis.Service.AuditLogServices;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * AOP aspect to create audit logs for methods annotated with @Auditable.
 */
@Aspect
@Component
@Slf4j
@AllArgsConstructor
public class AuditAspect {

    private final AuditLogServices auditLogServices;
    private final HttpServletRequest request;
    private final ObjectMapper objectMapper;

    /**
     * Runs before method execution and stores audit context.
     */
    @Before("@annotation(auditable)")
    public void beforeAuditableMethod(JoinPoint joinPoint, Auditable auditable) {
        try {
            log.debug("AuditAspect @Before advice triggered for: {}", joinPoint.getSignature());

            Object[] args = joinPoint.getArgs();

            Long entityId = extractEntityId(args, auditable);

            if (request != null) {
                request.setAttribute("audit_entity_id", entityId);
                log.debug("Stored audit context in request scope: entityId={}", entityId);
            }
        } catch (Exception e) {
            log.debug("Error in audit aspect @Before advice", e);
        }
    }

    /**
     * Runs after successful method execution.
     */
    @AfterReturning(value = "@annotation(auditable)", returning = "result")
    public void afterAuditableMethodSuccess(JoinPoint joinPoint, Auditable auditable, Object result) {
        try {
            log.debug("AuditAspect @AfterReturning advice triggered: success case");

            Long userId = getCurrentUserId();

            Long entityId = (Long) request.getAttribute("audit_entity_id");
            Object newValues = null;

            if (result != null) {
                newValues = objectMapper.convertValue(result, Map.class);
                log.debug("Converted method result to Map: {} fields",
                        newValues instanceof Map ? ((Map<?, ?>) newValues).size() : 0);
            }

            auditLogServices.logAction(
                    userId,
                    auditable.entityType(),
                    entityId,
                    AuditAction.valueOf(auditable.action()),
                    null,
                    newValues,
                    request,
                    AuditStatus.SUCCESS,
                    null);

            log.debug("SUCCESS audit log recorded for: {} on {}", auditable.action(), auditable.entityType());

        } catch (Exception e) {
            log.error("Error logging successful audit action in @AfterReturning", e);
        }
    }

    /**
     * Runs after method throws an exception.
     */
    @AfterThrowing(value = "@annotation(auditable)", throwing = "exception")
    public void afterAuditableMethodException(JoinPoint joinPoint, Auditable auditable, Exception exception) {
        try {
            log.debug("AuditAspect @AfterThrowing advice triggered: exception case");

            Long userId = getCurrentUserId();

            Long entityId = (Long) request.getAttribute("audit_entity_id");

            log.warn("Caught exception in auditable method: {}", exception.getMessage());

            auditLogServices.logAction(
                    userId,
                    auditable.entityType(),
                    entityId,
                    AuditAction.valueOf(auditable.action()),
                    null,
                    null,
                    request,
                    AuditStatus.FAILED,
                    exception.getMessage());

            log.debug("FAILED audit log recorded: {} on {} - reason: {}",
                    auditable.action(), auditable.entityType(), exception.getMessage());

        } catch (Exception e) {
            log.error("Error logging failed audit action in @AfterThrowing", e);
        }
    }

    /**
     * Extract entity id from method arguments.
     */
    private Long extractEntityId(Object[] args, Auditable auditable) {
        String expression = auditable.entityIdExpression();

        if (expression.isEmpty() && args.length > 0 && args[0] instanceof Long) {
            Long entityId = (Long) args[0];
            log.debug("Extracted entity ID from first argument: {}", entityId);
            return entityId;
        }

        return null;
    }

    /**
     * Get current user id from Spring Security context.
     */
    private Long getCurrentUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth != null && auth.isAuthenticated()) {
                Object principal = auth.getPrincipal();

                if (principal instanceof UserEntity) {
                    UserEntity user = (UserEntity) principal;
                    Long userId = user.getUserId();
                    log.debug("Extracted current user ID from security context: {}", userId);
                    return userId;
                }
            }
            log.debug("No authenticated user found in security context");
            return null;

        } catch (Exception e) {
            log.warn("Error extracting current user ID from security context", e);
            return null;
        }
    }
}
