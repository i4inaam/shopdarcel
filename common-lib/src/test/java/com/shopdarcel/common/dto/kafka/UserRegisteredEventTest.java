package com.shopdarcel.common.dto.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserRegisteredEventTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Test
    void superBuilder_combinesParentAndChildFieldsInOneChain() {
        UUID eventId = UUID.randomUUID();
        Instant now = Instant.now()
                .truncatedTo(ChronoUnit.MILLIS);

        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .eventId(eventId)            // from KafkaEvent (parent)
                .correlationId("corr-123")   // from KafkaEvent (parent)
                .occurredAt(now)              // from KafkaEvent (parent)
                .userId(42L)                  // from UserRegisteredEvent (child)
                .email("jane@example.com")    // from UserRegisteredEvent (child)
                .fullName("Jane Doe")
                .role("CUSTOMER")
                .build();

        assertThat(event.getEventId()).isEqualTo(eventId);
        assertThat(event.getCorrelationId()).isEqualTo("corr-123");
        assertThat(event.getOccurredAt()).isEqualTo(now);
        assertThat(event.getUserId()).isEqualTo(42L);
        assertThat(event.getEmail()).isEqualTo("jane@example.com");
    }

    @Test
    void serialization_writesOccurredAtAsIsoStringNotEpochNumber() throws Exception {
        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .eventId(UUID.randomUUID())
                .correlationId("corr-123")
                .occurredAt(Instant.parse("2026-07-02T10:00:00.123Z"))
                .userId(1L)
                .email("a@b.com")
                .fullName("A B")
                .role("CUSTOMER")
                .build();

        String json = objectMapper.writeValueAsString(event);

        assertThat(json).contains("\"occurredAt\":\"2026-07-02T10:00:00.123Z\"");
    }

    @Test
    void deserialization_roundTripsBackToAnEquivalentObject() throws Exception {
        UserRegisteredEvent original = UserRegisteredEvent.builder()
                .eventId(UUID.randomUUID())
                .correlationId("corr-456")
                .occurredAt(Instant.now()
                        .truncatedTo(ChronoUnit.MILLIS))
                .userId(7L)
                .email("test@test.com")
                .fullName("Test User")
                .role("ADMIN")
                .build();

        String json = objectMapper.writeValueAsString(original);
        UserRegisteredEvent deserialized = objectMapper.readValue(json, UserRegisteredEvent.class);

        assertThat(deserialized).isEqualTo(original);
    }
}