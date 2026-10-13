package com.bookmyshow.master.infrastructure.persistence;

import com.bookmyshow.master.domain.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataLanguageRepository extends JpaRepository<Language, UUID> {

    Optional<Language> findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByNameIgnoreCase(String name);

    @Query("SELECT l FROM Language l WHERE l.status = 'ACTIVE' ORDER BY l.name ASC")
    List<Language> findAllActive();
}
