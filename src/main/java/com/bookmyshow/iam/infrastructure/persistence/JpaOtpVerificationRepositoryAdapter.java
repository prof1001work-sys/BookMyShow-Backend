package com.bookmyshow.iam.infrastructure.persistence;

import com.bookmyshow.iam.domain.model.OtpPurpose;
import com.bookmyshow.iam.domain.model.OtpVerification;
import com.bookmyshow.iam.domain.repository.OtpVerificationRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class JpaOtpVerificationRepositoryAdapter implements OtpVerificationRepository {

    private final SpringDataOtpVerificationRepository springDataOtpVerificationRepository;

    public JpaOtpVerificationRepositoryAdapter(SpringDataOtpVerificationRepository springDataOtpVerificationRepository) {
        this.springDataOtpVerificationRepository = springDataOtpVerificationRepository;
    }

    @Override
    public OtpVerification save(OtpVerification otpVerification) {
        return springDataOtpVerificationRepository.save(otpVerification);
    }

    @Override
    public Optional<OtpVerification> findLatestByUserIdAndPurpose(UUID userId, OtpPurpose purpose) {
        return springDataOtpVerificationRepository.findLatestByUserIdAndPurpose(userId, purpose);
    }

    @Override
    public void deleteByUserIdAndPurpose(UUID userId, OtpPurpose purpose) {
        springDataOtpVerificationRepository.deleteByUserIdAndPurpose(userId, purpose);
    }
}
