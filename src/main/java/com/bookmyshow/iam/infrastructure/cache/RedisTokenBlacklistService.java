package com.bookmyshow.iam.infrastructure.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Service managing blacklisted JWT IDs (jti) in Redis for instantaneous logout revocation.
 */
@Service
public class RedisTokenBlacklistService {

    private static final Logger log = LoggerFactory.getLogger(RedisTokenBlacklistService.class);
    private static final String BLACKLIST_PREFIX = "jwt:blacklist:";

    private final StringRedisTemplate redisTemplate;

    public RedisTokenBlacklistService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void blacklistToken(String jti, Duration remainingTtl) {
        if (jti == null || remainingTtl == null || remainingTtl.isNegative() || remainingTtl.isZero()) {
            return;
        }
        try {
            redisTemplate.opsForValue().set(BLACKLIST_PREFIX + jti, "revoked", remainingTtl);
            log.debug("Blacklisted JWT jti: {} for duration: {}s", jti, remainingTtl.getSeconds());
        } catch (Exception e) {
            log.error("Failed to blacklist JWT in Redis: {}", e.getMessage());
        }
    }

    public boolean isBlacklisted(String jti) {
        if (jti == null) {
            return false;
        }
        try {
            Boolean exists = redisTemplate.hasKey(BLACKLIST_PREFIX + jti);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.warn("Redis connectivity error checking blacklist for jti {}: {}", jti, e.getMessage());
            return false;
        }
    }
}
