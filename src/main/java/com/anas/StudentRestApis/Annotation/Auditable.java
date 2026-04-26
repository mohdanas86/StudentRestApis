package com.anas.StudentRestApis.Annotation;

import java.lang.annotation.*;

/**
 * Marks a method for audit logging via AuditAspect.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Auditable {

    /** Entity type for this audit event. */
    String entityType();

    /** Action name (must match AuditAction). */
    String action();

    /** Capture old values before update. */
    boolean captureOldValues() default false;

    /** Optional SpEL expression to resolve entity id. */
    String entityIdExpression() default "";
}
