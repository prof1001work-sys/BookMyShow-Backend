package com.bookmyshow.master.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateGenreRequest(
        @NotBlank(message = "Genre name is required")
        @Size(max = 50, message = "Genre name cannot exceed 50 characters")
        String name,

        @NotBlank(message = "Genre code is required")
        @Size(max = 50, message = "Genre code cannot exceed 50 characters")
        String code,

        @Size(max = 255, message = "Description cannot exceed 255 characters")
        String description
) {
}
