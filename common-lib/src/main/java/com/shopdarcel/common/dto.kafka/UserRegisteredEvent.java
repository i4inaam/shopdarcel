package com.shopdarcel.common.dto.kafka;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Published to {@code KafkaTopics.USER_REGISTERED} by user-service
 * immediately after a new account is successfully persisted.
 *
 * <p>Consumed by: notification-service (sends a welcome email).
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class UserRegisteredEvent extends KafkaEvent {

    /**
     * ID of the newly created user.
     */
    private Long userId;

    /**
     * Email address the user registered with.
     */
    private String email;

    /**
     * User's full name, for personalizing the welcome notification.
     */
    private String fullName;

    /**
     * Assigned role at registration time, e.g. "CUSTOMER".
     */
    private String role;
}