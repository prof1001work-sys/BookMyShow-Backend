package com.bookmyshow.master.api.publicapi;

import java.util.Optional;
import java.util.UUID;

/**
 * Public Java interface exposed by Master Data module to other modules in the Modular Monolith.
 */
public interface MasterDataModuleApi {

    Optional<CityPublicDto> findCityById(UUID cityId);

    boolean isValidCity(UUID cityId);

    Optional<LanguagePublicDto> findLanguageById(UUID languageId);

    Optional<LanguagePublicDto> findLanguageByCode(String code);

    boolean isValidLanguage(UUID languageId);

    Optional<GenrePublicDto> findGenreById(UUID genreId);

    boolean isValidGenre(UUID genreId);

    Optional<CertificationPublicDto> findCertificationById(UUID certificationId);

    Optional<CertificationPublicDto> findCertificationByCode(String code);

    boolean isValidCertification(UUID certificationId);
}
