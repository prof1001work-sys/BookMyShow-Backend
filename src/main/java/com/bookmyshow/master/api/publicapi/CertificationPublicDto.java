package com.bookmyshow.master.api.publicapi;

import java.io.Serializable;
import java.util.UUID;

/**
 * Public DTO representing a Certification rating exposed to other business modules.
 */
public record CertificationPublicDto(
        UUID id,
        String code,
        String name,
        String description,
        int minimumAge
) implements Serializable {
}
