package com.bookmyshow.master.application;

import com.bookmyshow.master.api.dto.CertificationResponse;
import com.bookmyshow.master.api.dto.CreateCertificationRequest;
import com.bookmyshow.master.api.dto.CreateGenreRequest;
import com.bookmyshow.master.api.dto.CreateLanguageRequest;
import com.bookmyshow.master.api.dto.GenreResponse;
import com.bookmyshow.master.api.dto.LanguageResponse;
import com.bookmyshow.master.domain.model.Certification;
import com.bookmyshow.master.domain.model.Genre;
import com.bookmyshow.master.domain.model.Language;
import com.bookmyshow.master.domain.repository.CertificationRepository;
import com.bookmyshow.master.domain.repository.GenreRepository;
import com.bookmyshow.master.domain.repository.LanguageRepository;
import com.bookmyshow.shared.configuration.RedisCacheConfig;
import com.bookmyshow.shared.exception.ConflictException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application service for Languages, Genres, and Certifications with Redis caching.
 */
@Service
public class MasterDataService {

    private static final Logger log = LoggerFactory.getLogger(MasterDataService.class);

    private final LanguageRepository languageRepository;
    private final GenreRepository genreRepository;
    private final CertificationRepository certificationRepository;

    public MasterDataService(
            LanguageRepository languageRepository,
            GenreRepository genreRepository,
            CertificationRepository certificationRepository
    ) {
        this.languageRepository = languageRepository;
        this.genreRepository = genreRepository;
        this.certificationRepository = certificationRepository;
    }

    // --- Languages ---

    @Transactional(readOnly = true)
    @Cacheable(value = RedisCacheConfig.CACHE_LANGUAGES, key = "'all'")
    public List<LanguageResponse> getAllLanguages() {
        log.debug("Fetching all active languages from database (cache miss)");
        return languageRepository.findAllActive().stream()
                .map(LanguageResponse::from)
                .toList();
    }

    @Transactional
    @CacheEvict(value = RedisCacheConfig.CACHE_LANGUAGES, allEntries = true)
    public LanguageResponse createLanguage(CreateLanguageRequest request) {
        if (languageRepository.existsByCode(request.code().trim())) {
            throw new ConflictException("Language with code '" + request.code() + "' already exists");
        }
        if (languageRepository.existsByName(request.name().trim())) {
            throw new ConflictException("Language with name '" + request.name() + "' already exists");
        }

        Language language = new Language(request.name().trim(), request.code().trim().toLowerCase());
        Language saved = languageRepository.save(language);
        log.info("Created language: {} ({}) with ID: {}", saved.getName(), saved.getCode(), saved.getId());
        return LanguageResponse.from(saved);
    }

    // --- Genres ---

    @Transactional(readOnly = true)
    @Cacheable(value = RedisCacheConfig.CACHE_GENRES, key = "'all'")
    public List<GenreResponse> getAllGenres() {
        log.debug("Fetching all active genres from database (cache miss)");
        return genreRepository.findAllActive().stream()
                .map(GenreResponse::from)
                .toList();
    }

    @Transactional
    @CacheEvict(value = RedisCacheConfig.CACHE_GENRES, allEntries = true)
    public GenreResponse createGenre(CreateGenreRequest request) {
        if (genreRepository.existsByCode(request.code().trim())) {
            throw new ConflictException("Genre with code '" + request.code() + "' already exists");
        }
        if (genreRepository.existsByName(request.name().trim())) {
            throw new ConflictException("Genre with name '" + request.name() + "' already exists");
        }

        Genre genre = new Genre(
                request.name().trim(),
                request.code().trim().toUpperCase(),
                request.description() != null ? request.description().trim() : null
        );
        Genre saved = genreRepository.save(genre);
        log.info("Created genre: {} ({}) with ID: {}", saved.getName(), saved.getCode(), saved.getId());
        return GenreResponse.from(saved);
    }

    // --- Certifications ---

    @Transactional(readOnly = true)
    @Cacheable(value = RedisCacheConfig.CACHE_CERTIFICATIONS, key = "'all'")
    public List<CertificationResponse> getAllCertifications() {
        log.debug("Fetching all certifications from database (cache miss)");
        return certificationRepository.findAll().stream()
                .map(CertificationResponse::from)
                .toList();
    }

    @Transactional
    @CacheEvict(value = RedisCacheConfig.CACHE_CERTIFICATIONS, allEntries = true)
    public CertificationResponse createCertification(CreateCertificationRequest request) {
        if (certificationRepository.existsByCode(request.code().trim())) {
            throw new ConflictException("Certification with code '" + request.code() + "' already exists");
        }

        Certification cert = new Certification(
                request.code().trim().toUpperCase(),
                request.name().trim(),
                request.description() != null ? request.description().trim() : null,
                request.minimumAge()
        );
        Certification saved = certificationRepository.save(cert);
        log.info("Created certification: {} ({}) with ID: {}", saved.getName(), saved.getCode(), saved.getId());
        return CertificationResponse.from(saved);
    }
}
