package com.bookmyshow.master.domain.repository;

import com.bookmyshow.master.domain.model.Language;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository contract for Language entity.
 */
public interface LanguageRepository {

    Language save(Language language);

    Optional<Language> findById(UUID id);

    Optional<Language> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByName(String name);

    List<Language> findAllActive();
}
