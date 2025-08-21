package com.blueisfresh.bootguard.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Utility class for authentication-related helpers.
 * <p>
 * Provides methods to retrieve the current authenticated username
 * from the Spring Security context.
 */

@Component
public class AuthUtils {

    // gets the Current user from the Security Context
    public String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }
        return auth.getName();
    }
}
