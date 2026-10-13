package com.bookmyshow.master.infrastructure.persistence;

import com.bookmyshow.master.domain.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataGenreRepository extends JpaRepository<Genre, UUID> {

    Optional<Genre> findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByNameIgnoreCase(String name);

    @Query("SELECT g FROM Genre g WHERE g.status = 'ACTIVE' ORDER BY g.name ASC")
    List<Genre> findAllActive();
}
