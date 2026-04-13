package com.anas.StudentRestApis.Entity;

import lombok.*;

/**
 * Role Enum - Define authorization levels for the system
 *
 * Design Decision: Using enum instead of database table because:
 * 1. Roles are fixed and system-level (not user-configurable)
 * 2. Easier to deploy and version control
 * 3. Type-safe in code
 * 4. Better performance (no DB lookup)
 */
@Getter
@AllArgsConstructor
public enum Role {
    ADMIN("ROLE_ADMIN", "Full system access - can manage all resources, users, and permissions"),
    TEACHER("ROLE_TEACHER", "Can manage courses, view student grades, update course materials"),
    STUDENT("ROLE_STUDENT", "Can view courses, submit assignments, view their own grades");

    private final String authority;
    private final String description;

    /**
     * Check if this role has a specific authority
     */
    public boolean hasAuthority(String requiredAuthority){
        return this.authority.equals(requiredAuthority);
    }

    /**
     * Get role from string (case-insensitive)
     */
    public static Role fromString(String roleString){
        for(Role role: Role.values()){
            if(role.name().equalsIgnoreCase(roleString)){
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + roleString);
    }
}
