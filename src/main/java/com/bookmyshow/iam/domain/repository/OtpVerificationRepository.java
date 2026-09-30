package com.bookmyshow.iam.domain.repository;

import com.bookmyshow.iam.domain.model.OtpPurpose;
import com.bookmyshow.iam.domain.model.OtpVerification;

import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository contract for OtpVerification entity.
 */
public interface OtpVerificationRepository {

    OtpVerification save(OtpVerification otpVerification);

    Optional<OtpVerification> findLatestByUserIdAndPurpose(UUID userId, OtpPurpose purpose);

    void deleteByUserIdAndPurpose(UUID userId, OtpPurpose purpose);
}
