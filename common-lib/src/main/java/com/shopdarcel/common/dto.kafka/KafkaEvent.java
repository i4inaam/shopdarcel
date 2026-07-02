package com.shopdarcel.common.dto.kafka;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

/**
 * Base class every Kafka event DTO in {@code dto/kafka} extends.
 *
 * <p>Carries the three fields every event needs regardless of domain:
 * <ul>
 *   <li>{@code eventId} — unique ID of this specific event instance, useful
 *       for consumer-side idempotency (dedupe on eventId before processing)</li>
 *   <li>{@code correlationId} — propagated from the originating HTTP
 *       request's {@code X-Correlation-Id} header, so a single user
 *       action can be traced across every service and topic it touches</li>
 *   <li>{@code occurredAt} — when the event was produced, independent of
 *       Kafka's own record timestamp</li>
 * </ul>
 *
 * <p>Uses Lombok's {@code @SuperBuilder} (rather than plain
 * {@code @Builder}) specifically so that subclasses can each declare their
 * own {@code @SuperBuilder} and get a fluent builder that includes both
 * these base fields and their own domain fields in one chain.
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode
public abstract class KafkaEvent {

    /**
     * Unique ID of this event instance — generate with {@code UUID.randomUUID()} at publish time.
     */
    private UUID eventId;

    /**
     * Correlation ID propagated from the originating request, for cross-service tracing.
     */
    private String correlationId;

    /**
     * UTC instant the event was produced.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant occurredAt;
}