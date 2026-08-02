package com.facilityflow.entity;

/**
 * Application-level roles. Kept as an enum (rather than a table) since the
 * role set is fixed and small; simplifies JWT claim handling and
 * {@code @PreAuthorize} expressions across the codebase.
 */
public enum Role {
    ADMIN,
    FACILITY_MANAGER,
    EMPLOYEE
}
