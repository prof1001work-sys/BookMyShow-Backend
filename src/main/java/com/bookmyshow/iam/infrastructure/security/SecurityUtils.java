package com.bookmyshow.iam.infrastructure.security;

import com.bookmyshow.shared.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

/**
 * Security utility class to retrieve current authenticated user details from SecurityContext.
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() == null) {
            throw new UnauthorizedException("No authenticated security context found");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UUID uuid) {
            return uuid;
        } else if (principal instanceof String str) {
            try {
                return UUID.fromString(str);
            } catch (IllegalArgumentException e) {
                throw new UnauthorizedException("Invalid user identifier in security context: " + str);
            }
        } else if (principal instanceof SecurityUser securityUser) {
            return securityUser.getId();
        }

        throw new UnauthorizedException("Unable to resolve authenticated user ID from principal: " + principal);
    }
}
