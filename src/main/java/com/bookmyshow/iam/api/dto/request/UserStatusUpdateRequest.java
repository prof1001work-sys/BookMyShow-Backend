package com.bookmyshow.iam.api.dto.request;

import com.bookmyshow.iam.domain.model.UserStatus;
import jakarta.validation.constraints.NotNull;

public record UserStatusUpdateRequest(
        @NotNull(message = "Status is required")
        UserStatus status
) {
}
