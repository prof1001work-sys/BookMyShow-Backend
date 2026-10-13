package com.bookmyshow.master.api.dto;

import com.bookmyshow.master.domain.model.Genre;

import java.io.Serializable;
import java.util.UUID;

public record GenreResponse(
        UUID id,
        String name,
        String code,
        String description,
        String status
) implements Serializable {

    public static GenreResponse from(Genre genre) {
        return new GenreResponse(
                genre.getId(),
                genre.getName(),
                genre.getCode(),
                genre.getDescription(),
                genre.getStatus().name()
        );
    }
}
