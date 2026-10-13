package com.bookmyshow.master.domain.repository;

import com.bookmyshow.master.domain.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository contract for Genre entity.
 */
public interface GenreRepository {

    Genre save(Genre genre);

    Optional<Genre> findById(UUID id);

    Optional<Genre> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByName(String name);

    List<Genre> findAllActive();
}
