package com.bookmyshow.master.api.dto;

import com.bookmyshow.master.domain.model.Certification;

import java.io.Serializable;
import java.util.UUID;

public record CertificationResponse(
        UUID id,
        String code,
        String name,
        String description,
        int minimumAge
) implements Serializable {

    public static CertificationResponse from(Certification cert) {
        return new CertificationResponse(
                cert.getId(),
                cert.getCode(),
                cert.getName(),
                cert.getDescription(),
                cert.getMinimumAge()
        );
    }
}
