package com.bookmyshow.iam.infrastructure.persistence;

import com.bookmyshow.iam.domain.model.OtpPurpose;
import com.bookmyshow.iam.domain.model.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataOtpVerificationRepository extends JpaRepository<OtpVerification, UUID> {

    @Query("SELECT o FROM OtpVerification o WHERE o.user.id = :userId AND o.purpose = :purpose ORDER BY o.createdAt DESC LIMIT 1")
    Optional<OtpVerification> findLatestByUserIdAndPurpose(@Param("userId") UUID userId, @Param("purpose") OtpPurpose purpose);

    @Modifying
    @Query("DELETE FROM OtpVerification o WHERE o.user.id = :userId AND o.purpose = :purpose")
    void deleteByUserIdAndPurpose(@Param("userId") UUID userId, @Param("purpose") OtpPurpose purpose);
}
