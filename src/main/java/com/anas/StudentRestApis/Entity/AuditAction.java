package com.anas.StudentRestApis.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Supported audit actions.
 */
@Getter
@AllArgsConstructor
public enum AuditAction {
    /** Create action. */
    CREATE("Create"),

    /** Read action. */
    READ("Read"),

    /** Update action. */
    UPDATE("Update"),

    /** Delete action. */
    DELETE("Delete"),

    /** Login action. */
    LOGIN("Login"),

    /** Logout action. */
    LOGOUT("Logout"),

    /** Permission denied action. */
    PERMISSION_DENIED("Permission Denied"),

    /** Invalid login action. */
    INVALID_LOGIN("Invalid Login");

    /** Display label. */
    private String description;
}
