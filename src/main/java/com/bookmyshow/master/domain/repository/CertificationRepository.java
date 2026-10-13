package com.bookmyshow.master.domain.repository;

import com.bookmyshow.master.domain.model.Certification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository contract for Certification entity.
 */
public interface CertificationRepository {

    Certification save(Certification certification);

    Optional<Certification> findById(UUID id);

    Optional<Certification> findByCode(String code);

    boolean existsByCode(String code);

    List<Certification> findAll();
}
