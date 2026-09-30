package com.bookmyshow.iam.domain.event;

import com.bookmyshow.shared.event.DomainEvent;
import com.bookmyshow.shared.filter.CorrelationIdFilter;
import org.slf4j.MDC;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a user password is changed or reset.
 */
public record UserPasswordChangedEvent(
        UUID eventId,
        String eventType,
        String aggregateId,
        Instant occurredAt,
        String correlationId,
        String email
) implements DomainEvent {

    public UserPasswordChangedEvent(UUID userId, String email) {
        this(
                UUID.randomUUID(),
                "UserPasswordChangedEvent",
                userId.toString(),
                Instant.now(),
                resolveCorrelationId(),
                email
        );
    }

    private static String resolveCorrelationId() {
        String cid = MDC.get(CorrelationIdFilter.MDC_CORRELATION_ID_KEY);
        return cid != null ? cid : UUID.randomUUID().toString();
    }
}
