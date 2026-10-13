package com.bookmyshow.shared.configuration;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Platform Redis Cache Configuration.
 * Configures JSON serialization and cache-specific TTL policies.
 */
@Configuration
public class RedisCacheConfig {

    public static final String CACHE_CITIES = "cities";
    public static final String CACHE_POPULAR_CITIES = "popular-cities";
    public static final String CACHE_CITY_DETAIL = "city-detail";
    public static final String CACHE_LANGUAGES = "languages";
    public static final String CACHE_GENRES = "genres";
    public static final String CACHE_CERTIFICATIONS = "certifications";

    @Bean
    public RedisCacheConfiguration defaultCacheConfiguration(ObjectMapper objectMapper) {
        ObjectMapper cacheObjectMapper = objectMapper.copy();
        cacheObjectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .disableCachingNullValues()
                .prefixCacheNameWith("bms:")
                .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(cacheObjectMapper)));
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer(RedisCacheConfiguration defaultCacheConfig) {
        return builder -> builder
                .withCacheConfiguration(CACHE_CITIES, defaultCacheConfig.entryTtl(Duration.ofHours(24)))
                .withCacheConfiguration(CACHE_POPULAR_CITIES, defaultCacheConfig.entryTtl(Duration.ofHours(24)))
                .withCacheConfiguration(CACHE_CITY_DETAIL, defaultCacheConfig.entryTtl(Duration.ofHours(24)))
                .withCacheConfiguration(CACHE_LANGUAGES, defaultCacheConfig.entryTtl(Duration.ofHours(24)))
                .withCacheConfiguration(CACHE_GENRES, defaultCacheConfig.entryTtl(Duration.ofHours(24)))
                .withCacheConfiguration(CACHE_CERTIFICATIONS, defaultCacheConfig.entryTtl(Duration.ofHours(24)));
    }
}
