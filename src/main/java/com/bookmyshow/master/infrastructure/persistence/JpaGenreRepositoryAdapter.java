package com.bookmyshow.master.infrastructure.persistence;

import com.bookmyshow.master.domain.model.Genre;
import com.bookmyshow.master.domain.repository.GenreRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaGenreRepositoryAdapter implements GenreRepository {

    private final SpringDataGenreRepository springDataGenreRepository;

    public JpaGenreRepositoryAdapter(SpringDataGenreRepository springDataGenreRepository) {
        this.springDataGenreRepository = springDataGenreRepository;
    }

    @Override
    public Genre save(Genre genre) {
        return springDataGenreRepository.save(genre);
    }

    @Override
    public Optional<Genre> findById(UUID id) {
        return springDataGenreRepository.findById(id);
    }

    @Override
    public Optional<Genre> findByCode(String code) {
        return springDataGenreRepository.findByCodeIgnoreCase(code);
    }

    @Override
    public boolean existsByCode(String code) {
        return springDataGenreRepository.existsByCodeIgnoreCase(code);
    }

    @Override
    public boolean existsByName(String name) {
        return springDataGenreRepository.existsByNameIgnoreCase(name);
    }

    @Override
    public List<Genre> findAllActive() {
        return springDataGenreRepository.findAllActive();
    }
}
