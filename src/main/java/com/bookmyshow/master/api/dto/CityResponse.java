package com.bookmyshow.master.api.dto;

import com.bookmyshow.master.domain.model.City;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public record CityResponse(
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

    public static CityResponse from(City city) {
        return new CityResponse(
                city.getId(),
                city.getName(),
                city.getState(),
                city.getCountry(),
                city.getPincode(),
                city.isPopular(),
                city.getStatus().name(),
                city.getLatitude(),
                city.getLongitude()
        );
    }
}
