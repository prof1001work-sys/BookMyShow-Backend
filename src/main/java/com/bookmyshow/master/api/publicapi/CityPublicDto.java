package com.bookmyshow.master.api.publicapi;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Public DTO representing a City exposed to other business modules.
 */
public record CityPublicDto(
        UUID id,
        String name,
        String state,
        String country,
        String pincode,
        boolean isPopular,
        String status,
        BigDecimal latitude,
        BigDecimal longitude
) implements Serializable {
}
