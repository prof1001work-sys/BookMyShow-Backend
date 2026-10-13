package com.bookmyshow.master.infrastructure.persistence;

import com.bookmyshow.master.domain.model.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataCertificationRepository extends JpaRepository<Certification, UUID> {

    Optional<Certification> findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCase(String code);

    @Query("SELECT c FROM Certification c ORDER BY c.minimumAge ASC")
    List<Certification> findAllOrdered();
}
