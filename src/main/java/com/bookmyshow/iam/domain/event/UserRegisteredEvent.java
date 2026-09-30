package com.bookmyshow.iam.domain.event;

import com.bookmyshow.shared.event.DomainEvent;
import com.bookmyshow.shared.filter.CorrelationIdFilter;
import org.slf4j.MDC;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * Domain event emitted when a new user completes registration.
 */
public record UserRegisteredEvent(
        UUID eventId,
        String eventType,
        String aggregateId,
        Instant occurredAt,
        String correlationId,
        String email,
        String fullName,
        Set<String> roles
) implements DomainEvent {

    public UserRegisteredEvent(UUID userId, String email, String fullName, Set<String> roles) {
        this(
                UUID.randomUUID(),
                "UserRegisteredEvent",
                userId.toString(),
                Instant.now(),
                resolveCorrelationId(),
                email,
                fullName,
                roles
        );
    }

    private static String resolveCorrelationId() {
        String cid = MDC.get(CorrelationIdFilter.MDC_CORRELATION_ID_KEY);
        return cid != null ? cid : UUID.randomUUID().toString();
    }
}
