package com.anas.StudentRestApis.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Result status for audit events.
 */
@Getter
@AllArgsConstructor
public enum AuditStatus {
    /** Successful operation. */
    SUCCESS("Success"),

    /** Failed operation. */
    FAILED("Failed");

    /** Display label. */
    private String description;
}
