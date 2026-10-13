package com.bookmyshow.master.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCertificationRequest(
        @NotBlank(message = "Certification code is required")
        @Size(max = 10, message = "Certification code cannot exceed 10 characters")
        String code,

        @NotBlank(message = "Certification name is required")
        @Size(max = 50, message = "Certification name cannot exceed 50 characters")
        String name,

        @Size(max = 255, message = "Description cannot exceed 255 characters")
        String description,

        @Min(value = 0, message = "Minimum age cannot be negative")
        int minimumAge
) {
}
