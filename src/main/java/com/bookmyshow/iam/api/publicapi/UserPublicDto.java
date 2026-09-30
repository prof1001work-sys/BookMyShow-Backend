package com.bookmyshow.iam.api.publicapi;

import java.util.Set;
import java.util.UUID;

/**
 * Public DTO exposed to other business modules to reference user details.
 */
public record UserPublicDto(
        UUID id,
        String email,
        String fullName,
        String phoneNumber,
        Set<String> roles,
        String status
) {
}
