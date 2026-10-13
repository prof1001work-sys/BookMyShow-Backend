package com.bookmyshow.master.api.publicapi;

import java.io.Serializable;
import java.util.UUID;

/**
 * Public DTO representing a Genre exposed to other business modules.
 */
public record GenrePublicDto(
        UUID id,
        String name,
        String code,
        String description,
        String status
) implements Serializable {
}
