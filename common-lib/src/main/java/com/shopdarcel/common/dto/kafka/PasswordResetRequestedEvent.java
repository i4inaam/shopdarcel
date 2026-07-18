package com.shopdarcel.common.dto.kafka;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

/**
 * Published to {@code KafkaTopics.USER_PASSWORD_RESET_REQUESTED} by
 * user-service when a user requests a password reset.
 *
 * <p>Consumed by: notification-service (emails the reset link containing
 * {@code resetToken}).
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class PasswordResetRequestedEvent extends KafkaEvent {

    /**
     * ID of the user requesting the reset.
     */
    private Long userId;

    /**
     * Email address to send the reset link to.
     */
    private String email;

    /**
     * Single-use token to include in the reset link.
     */
    private String resetToken;

    /**
     * When this reset token stops being valid.
     */
    private Instant expiresAt;
}