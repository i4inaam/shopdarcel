package com.shopdarcel.common.dto.kafka;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Published to {@code KafkaTopics.USER_ACCOUNT_LOCKED} by user-service
 * once an account is locked after too many failed login attempts (see
 * {@code failedLoginAttempts} / {@code accountLockedAt} on the User entity).
 *
 * <p>Consumed by: notification-service (sends a security alert email).
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class AccountLockedEvent extends KafkaEvent {

    /**
     * ID of the locked user account.
     */
    private Long userId;

    /**
     * Email address to notify.
     */
    private String email;
}