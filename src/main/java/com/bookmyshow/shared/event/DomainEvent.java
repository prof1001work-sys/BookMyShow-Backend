package com.bookmyshow.shared.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Base contract for domain events published across modules or to the transactional outbox.
 */
public interface DomainEvent {

    UUID eventId();

    String eventType();

    String aggregateId();

    Instant occurredAt();

    String correlationId();
}
