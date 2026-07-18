package com.shopdarcel.common.dto.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserLifecycleEventsTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Test
    void passwordChangedEvent_buildsWithParentAndChildFields() {
        PasswordChangedEvent event = PasswordChangedEvent.builder()
                .eventId(UUID.randomUUID())
                .correlationId("corr-1")
                .occurredAt(Instant.now())
                .userId(1L)
                .email("a@b.com")
                .build();

        assertThat(event.getUserId()).isEqualTo(1L);
        assertThat(event.getEmail()).isEqualTo("a@b.com");
        assertThat(event.getOccurredAt()).isNotNull();
    }

    @Test
    void accountLockedEvent_buildsWithParentAndChildFields() {
        AccountLockedEvent event = AccountLockedEvent.builder()
                .eventId(UUID.randomUUID())
                .correlationId("corr-2")
                .occurredAt(Instant.now())
                .userId(2L)
                .email("locked@b.com")
                .build();

        assertThat(event.getUserId()).isEqualTo(2L);
        assertThat(event.getEmail()).isEqualTo("locked@b.com");
    }

    @Test
    void accountDeactivatedEvent_buildsWithParentAndChildFields() {
        AccountDeactivatedEvent event = AccountDeactivatedEvent.builder()
                .eventId(UUID.randomUUID())
                .correlationId("corr-3")
                .occurredAt(Instant.now())
                .userId(3L)
                .email("bye@b.com")
                .build();

        assertThat(event.getUserId()).isEqualTo(3L);
        assertThat(event.getEmail()).isEqualTo("bye@b.com");
    }

    @Test
    void passwordResetRequestedEvent_serializesAndDeserializesCorrectly() throws Exception {
        Instant expiry = Instant.now()
                .plusSeconds(900)
                .truncatedTo(ChronoUnit.MILLIS);

        PasswordResetRequestedEvent original = PasswordResetRequestedEvent.builder()
                .eventId(UUID.randomUUID())
                .correlationId("corr-4")
                .occurredAt(Instant.now().truncatedTo(ChronoUnit.MILLIS))
                .userId(4L)
                .email("reset@b.com")
                .resetToken("abc123token")
                .expiresAt(expiry)
                .build();

        String json = objectMapper.writeValueAsString(original);
        PasswordResetRequestedEvent deserialized = objectMapper.readValue(json, PasswordResetRequestedEvent.class);

        assertThat(deserialized).isEqualTo(original);
        assertThat(deserialized.getExpiresAt()).isEqualTo(expiry);
    }

    @Test
    void emailVerificationRequestedEvent_buildsWithAllFields() {
        Instant expiry = Instant.now()
                .plusSeconds(3600);

        EmailVerificationRequestedEvent event = EmailVerificationRequestedEvent.builder()
                .eventId(UUID.randomUUID())
                .correlationId("corr-5")
                .occurredAt(Instant.now())
                .userId(5L)
                .email("verify@b.com")
                .verificationToken("xyz789token")
                .expiresAt(expiry)
                .build();

        assertThat(event.getVerificationToken()).isEqualTo("xyz789token");
        assertThat(event.getExpiresAt()).isEqualTo(expiry);
    }
}