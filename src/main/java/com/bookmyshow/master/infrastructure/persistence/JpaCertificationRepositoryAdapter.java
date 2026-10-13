package com.bookmyshow.master.infrastructure.persistence;

import com.bookmyshow.master.domain.model.Certification;
import com.bookmyshow.master.domain.repository.CertificationRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaCertificationRepositoryAdapter implements CertificationRepository {

    private final SpringDataCertificationRepository springDataCertificationRepository;

    public JpaCertificationRepositoryAdapter(SpringDataCertificationRepository springDataCertificationRepository) {
        this.springDataCertificationRepository = springDataCertificationRepository;
    }

    @Override
    public Certification save(Certification certification) {
        return springDataCertificationRepository.save(certification);
    }

    @Override
    public Optional<Certification> findById(UUID id) {
        return springDataCertificationRepository.findById(id);
    }

    @Override
    public Optional<Certification> findByCode(String code) {
        return springDataCertificationRepository.findByCodeIgnoreCase(code);
    }

    @Override
    public boolean existsByCode(String code) {
        return springDataCertificationRepository.existsByCodeIgnoreCase(code);
    }

    @Override
    public List<Certification> findAll() {
        return springDataCertificationRepository.findAllOrdered();
    }
}
