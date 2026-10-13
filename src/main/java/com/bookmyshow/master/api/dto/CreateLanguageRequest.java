package com.bookmyshow.master.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateLanguageRequest(
        @NotBlank(message = "Language name is required")
        @Size(max = 50, message = "Language name cannot exceed 50 characters")
        String name,

        @NotBlank(message = "Language code is required")
        @Size(max = 10, message = "Language code cannot exceed 10 characters")
        String code
) {
}
