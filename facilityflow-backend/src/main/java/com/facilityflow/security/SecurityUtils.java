package com.facilityflow.security;

import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Optional<UserPrincipal> getCurrentPrincipal() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            return Optional.empty();
        }
        return Optional.of(principal);
    }

    public static Long getCurrentUserId() {
        return getCurrentPrincipal().map(UserPrincipal::getId).orElse(null);
    }

    public static String getCurrentUserEmail() {
        return getCurrentPrincipal().map(UserPrincipal::getEmail).orElse("system");
    }
}
