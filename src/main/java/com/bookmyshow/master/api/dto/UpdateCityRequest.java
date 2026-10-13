package com.bookmyshow.master.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateCityRequest(
        @NotBlank(message = "City name is required")
        @Size(max = 100, message = "City name cannot exceed 100 characters")
        String name,

        @NotBlank(message = "State is required")
        @Size(max = 100, message = "State cannot exceed 100 characters")
        String state,

        @Size(max = 100, message = "Country cannot exceed 100 characters")
        String country,

        @Size(max = 20, message = "Pincode cannot exceed 20 characters")
        String pincode,

        Boolean isPopular,

        BigDecimal latitude,

        BigDecimal longitude
) {
}
