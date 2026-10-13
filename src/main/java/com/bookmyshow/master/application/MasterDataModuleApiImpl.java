package com.bookmyshow.master.application;

import com.bookmyshow.master.api.publicapi.CertificationPublicDto;
import com.bookmyshow.master.api.publicapi.CityPublicDto;
import com.bookmyshow.master.api.publicapi.GenrePublicDto;
import com.bookmyshow.master.api.publicapi.LanguagePublicDto;
import com.bookmyshow.master.api.publicapi.MasterDataModuleApi;
import com.bookmyshow.master.domain.model.MasterStatus;
import com.bookmyshow.master.domain.repository.CertificationRepository;
import com.bookmyshow.master.domain.repository.CityRepository;
import com.bookmyshow.master.domain.repository.GenreRepository;
import com.bookmyshow.master.domain.repository.LanguageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Inter-module facade implementing MasterDataModuleApi contract.
 */
@Service
public class MasterDataModuleApiImpl implements MasterDataModuleApi {

    private final CityRepository cityRepository;
    private final LanguageRepository languageRepository;
    private final GenreRepository genreRepository;
    private final CertificationRepository certificationRepository;

    public MasterDataModuleApiImpl(
            CityRepository cityRepository,
            LanguageRepository languageRepository,
            GenreRepository genreRepository,
            CertificationRepository certificationRepository
    ) {
        this.cityRepository = cityRepository;
        this.languageRepository = languageRepository;
        this.genreRepository = genreRepository;
        this.certificationRepository = certificationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CityPublicDto> findCityById(UUID cityId) {
        return cityRepository.findById(cityId)
                .map(city -> new CityPublicDto(
                        city.getId(),
                        city.getName(),
                        city.getState(),
                        city.getCountry(),
                        city.getPincode(),
                        city.isPopular(),
                        city.getStatus().name(),
                        city.getLatitude(),
                        city.getLongitude()
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isValidCity(UUID cityId) {
        return cityRepository.findById(cityId)
                .map(c -> c.getStatus() == MasterStatus.ACTIVE && !c.isDeleted())
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LanguagePublicDto> findLanguageById(UUID languageId) {
        return languageRepository.findById(languageId)
                .map(lang -> new LanguagePublicDto(
                        lang.getId(),
                        lang.getName(),
                        lang.getCode(),
                        lang.getStatus().name()
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LanguagePublicDto> findLanguageByCode(String code) {
        return languageRepository.findByCode(code)
                .map(lang -> new LanguagePublicDto(
                        lang.getId(),
                        lang.getName(),
                        lang.getCode(),
                        lang.getStatus().name()
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isValidLanguage(UUID languageId) {
        return languageRepository.findById(languageId)
                .map(l -> l.getStatus() == MasterStatus.ACTIVE)
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GenrePublicDto> findGenreById(UUID genreId) {
        return genreRepository.findById(genreId)
                .map(genre -> new GenrePublicDto(
                        genre.getId(),
                        genre.getName(),
                        genre.getCode(),
                        genre.getDescription(),
                        genre.getStatus().name()
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isValidGenre(UUID genreId) {
        return genreRepository.findById(genreId)
                .map(g -> g.getStatus() == MasterStatus.ACTIVE)
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CertificationPublicDto> findCertificationById(UUID certificationId) {
        return certificationRepository.findById(certificationId)
                .map(cert -> new CertificationPublicDto(
                        cert.getId(),
                        cert.getCode(),
                        cert.getName(),
                        cert.getDescription(),
                        cert.getMinimumAge()
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CertificationPublicDto> findCertificationByCode(String code) {
        return certificationRepository.findByCode(code)
                .map(cert -> new CertificationPublicDto(
                        cert.getId(),
                        cert.getCode(),
                        cert.getName(),
                        cert.getDescription(),
                        cert.getMinimumAge()
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isValidCertification(UUID certificationId) {
        return certificationRepository.findById(certificationId).isPresent();
    }
}
