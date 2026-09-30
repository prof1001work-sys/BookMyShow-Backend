package com.bookmyshow.iam.api.dto.response;

public record TokenRefreshResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresInSeconds
) {
    public TokenRefreshResponse(String accessToken, String refreshToken, long expiresInSeconds) {
        this(accessToken, refreshToken, "Bearer", expiresInSeconds);
    }
}
