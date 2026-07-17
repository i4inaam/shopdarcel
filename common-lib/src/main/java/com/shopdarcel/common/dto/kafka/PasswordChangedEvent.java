package com.shopdarcel.common.dto.kafka;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Published to {@code KafkaTopics.USER_PASSWORD_CHANGED} by user-service
 * whenever a user successfully changes their password.
 *
 * <p>Consumed by: notification-service (sends a "your password was
 * changed" security alert email).
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class PasswordChangedEvent extends KafkaEvent {

    /**
     * ID of the user whose password changed.
     */
    private Long userId;

    /**
     * Email address to notify.
     */
    private String email;
}