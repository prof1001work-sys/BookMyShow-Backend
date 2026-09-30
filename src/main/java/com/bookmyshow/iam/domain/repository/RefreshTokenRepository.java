package com.bookmyshow.iam.domain.repository;

import com.bookmyshow.iam.domain.model.RefreshToken;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository contract for RefreshToken entity.
 */
public interface RefreshTokenRepository {

    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findAllByUserId(UUID userId);

    void revokeAllByUserId(UUID userId);
}
