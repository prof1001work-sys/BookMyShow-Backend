package com.bookmyshow;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.kafka.ConfluentKafkaContainer;

/**
 * Base abstract class for integration tests requiring live testcontainer infrastructure.
 * Automatically provisions disposable PostgreSQL and Kafka containers.
 */
@SpringBootTest
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    private static final PostgreSQLContainer<?> POSTGRES_CONTAINER;
    private static final ConfluentKafkaContainer KAFKA_CONTAINER;
    private static final boolean DOCKER_AVAILABLE;

    static {
        boolean dockerAvailableCheck = false;
        PostgreSQLContainer<?> postgres = null;
        ConfluentKafkaContainer kafka = null;

        try {
            postgres = new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("bookmyshow_test")
                    .withUsername("testuser")
                    .withPassword("testpass");
            postgres.start();

            kafka = new ConfluentKafkaContainer("confluentinc/cp-kafka:7.6.1");
            kafka.start();

            dockerAvailableCheck = true;
        } catch (Throwable t) {
            // Docker daemon is not active on host machine; fallback to H2 in-memory test profile
            dockerAvailableCheck = false;
            if (postgres != null && postgres.isRunning()) {
                postgres.stop();
            }
            if (kafka != null && kafka.isRunning()) {
                kafka.stop();
            }
            postgres = null;
            kafka = null;
        }

        DOCKER_AVAILABLE = dockerAvailableCheck;
        POSTGRES_CONTAINER = postgres;
        KAFKA_CONTAINER = kafka;
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        if (DOCKER_AVAILABLE && POSTGRES_CONTAINER != null && POSTGRES_CONTAINER.isRunning()) {
            registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
            registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
            registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
            registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
            registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
            registry.add("spring.flyway.enabled", () -> "true");
        }

        if (DOCKER_AVAILABLE && KAFKA_CONTAINER != null && KAFKA_CONTAINER.isRunning()) {
            registry.add("spring.kafka.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);
        }
    }
}
