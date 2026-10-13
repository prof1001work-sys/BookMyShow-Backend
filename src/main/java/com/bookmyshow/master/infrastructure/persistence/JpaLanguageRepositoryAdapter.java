package com.bookmyshow.master.infrastructure.persistence;

import com.bookmyshow.master.domain.model.Language;
import com.bookmyshow.master.domain.repository.LanguageRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaLanguageRepositoryAdapter implements LanguageRepository {

    private final SpringDataLanguageRepository springDataLanguageRepository;

    public JpaLanguageRepositoryAdapter(SpringDataLanguageRepository springDataLanguageRepository) {
        this.springDataLanguageRepository = springDataLanguageRepository;
    }

    @Override
    public Language save(Language language) {
        return springDataLanguageRepository.save(language);
    }

    @Override
    public Optional<Language> findById(UUID id) {
        return springDataLanguageRepository.findById(id);
    }

    @Override
    public Optional<Language> findByCode(String code) {
        return springDataLanguageRepository.findByCodeIgnoreCase(code);
    }

    @Override
    public boolean existsByCode(String code) {
        return springDataLanguageRepository.existsByCodeIgnoreCase(code);
    }

    @Override
    public boolean existsByName(String name) {
        return springDataLanguageRepository.existsByNameIgnoreCase(name);
    }

    @Override
    public List<Language> findAllActive() {
        return springDataLanguageRepository.findAllActive();
    }
}
