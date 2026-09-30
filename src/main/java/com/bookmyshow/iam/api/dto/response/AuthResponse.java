package com.bookmyshow.iam.api.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresInSeconds,
        UserProfileResponse user
) {
    public AuthResponse(String accessToken, String refreshToken, long expiresInSeconds, UserProfileResponse user) {
        this(accessToken, refreshToken, "Bearer", expiresInSeconds, user);
    }
}
