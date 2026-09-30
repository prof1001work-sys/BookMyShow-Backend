package com.bookmyshow.iam.api.dto.response;

import java.util.Set;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String fullName,
        String phoneNumber,
        Set<String> roles,
        String status,
        boolean isEmailVerified,
        boolean isPhoneVerified
) {
}
