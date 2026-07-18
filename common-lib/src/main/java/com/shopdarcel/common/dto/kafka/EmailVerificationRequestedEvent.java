package com.shopdarcel.common.dto.kafka;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

/**
 * Published to {@code KafkaTopics.USER_EMAIL_VERIFICATION_REQUESTED} by
 * user-service when a user needs to (re)verify their email address.
 *
 * <p>Consumed by: notification-service (emails the verification link
 * containing {@code verificationToken}).
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class EmailVerificationRequestedEvent extends KafkaEvent {

    /**
     * ID of the user needing verification.
     */
    private Long userId;

    /**
     * Email address being verified.
     */
    private String email;

    /**
     * Single-use token to include in the verification link.
     */
    private String verificationToken;

    /**
     * When this verification token stops being valid.
     */
    private Instant expiresAt;
}