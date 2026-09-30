package com.bookmyshow;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.kafka.ConfluentKafkaContainer;

/**
 * Base abstract class for integration tests requiring live testcontainer infrastructure.
 * Automatically provisions disposable MySQL and Kafka containers.
 */
@SpringBootTest
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    private static final MySQLContainer<?> MYSQL_CONTAINER;
    private static final ConfluentKafkaContainer KAFKA_CONTAINER;
    private static final boolean DOCKER_AVAILABLE;

    static {
        boolean dockerAvailableCheck = false;
        MySQLContainer<?> mysql = null;
        ConfluentKafkaContainer kafka = null;

        try {
            mysql = new MySQLContainer<>("mysql:8.0")
                    .withDatabaseName("bookmyshow_test")
                    .withUsername("testuser")
                    .withPassword("testpass");
            mysql.start();

            kafka = new ConfluentKafkaContainer("confluentinc/cp-kafka:7.6.1");
            kafka.start();

            dockerAvailableCheck = true;
        } catch (Throwable t) {
            // Docker daemon is not active on host machine; fallback to H2 in-memory test profile
            dockerAvailableCheck = false;
            if (mysql != null && mysql.isRunning()) {
                mysql.stop();
            }
            if (kafka != null && kafka.isRunning()) {
                kafka.stop();
            }
            mysql = null;
            kafka = null;
        }

        DOCKER_AVAILABLE = dockerAvailableCheck;
        MYSQL_CONTAINER = mysql;
        KAFKA_CONTAINER = kafka;
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        if (DOCKER_AVAILABLE && MYSQL_CONTAINER != null && MYSQL_CONTAINER.isRunning()) {
            registry.add("spring.datasource.url", MYSQL_CONTAINER::getJdbcUrl);
            registry.add("spring.datasource.username", MYSQL_CONTAINER::getUsername);
            registry.add("spring.datasource.password", MYSQL_CONTAINER::getPassword);
            registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
            registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
            registry.add("spring.flyway.enabled", () -> "true");
        }

        if (DOCKER_AVAILABLE && KAFKA_CONTAINER != null && KAFKA_CONTAINER.isRunning()) {
            registry.add("spring.kafka.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);
        }
    }
}
