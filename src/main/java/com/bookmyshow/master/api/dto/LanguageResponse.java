package com.bookmyshow.master.api.dto;

import com.bookmyshow.master.domain.model.Language;

import java.io.Serializable;
import java.util.UUID;

public record LanguageResponse(
        UUID id,
        String name,
        String code,
        String status
) implements Serializable {

    public static LanguageResponse from(Language language) {
        return new LanguageResponse(
                language.getId(),
                language.getName(),
                language.getCode(),
                language.getStatus().name()
        );
    }
}
