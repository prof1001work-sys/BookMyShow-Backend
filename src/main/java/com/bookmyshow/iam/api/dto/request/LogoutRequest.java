package com.bookmyshow.iam.api.dto.request;

public record LogoutRequest(
        String refreshToken
) {
}
