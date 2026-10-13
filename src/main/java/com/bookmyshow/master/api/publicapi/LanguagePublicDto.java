package com.bookmyshow.master.api.publicapi;

import java.io.Serializable;
import java.util.UUID;

/**
 * Public DTO representing a Language exposed to other business modules.
 */
public record LanguagePublicDto(
        UUID id,
        String name,
        String code,
        String status
) implements Serializable {
}
