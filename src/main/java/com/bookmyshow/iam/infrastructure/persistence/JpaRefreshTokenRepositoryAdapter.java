package com.bookmyshow.iam.infrastructure.persistence;

import com.bookmyshow.iam.domain.model.RefreshToken;
import com.bookmyshow.iam.domain.repository.RefreshTokenRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaRefreshTokenRepositoryAdapter implements RefreshTokenRepository {

    private final SpringDataRefreshTokenRepository springDataRefreshTokenRepository;

    public JpaRefreshTokenRepositoryAdapter(SpringDataRefreshTokenRepository springDataRefreshTokenRepository) {
        this.springDataRefreshTokenRepository = springDataRefreshTokenRepository;
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        return springDataRefreshTokenRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return springDataRefreshTokenRepository.findByTokenHashWithUser(tokenHash);
    }

    @Override
    public List<RefreshToken> findAllByUserId(UUID userId) {
        return springDataRefreshTokenRepository.findAllByUserId(userId);
    }

    @Override
    public void revokeAllByUserId(UUID userId) {
        springDataRefreshTokenRepository.revokeAllByUserId(userId);
    }
}
