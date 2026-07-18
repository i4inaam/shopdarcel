package com.shopdarcel.common.dto.kafka;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Published to {@code KafkaTopics.USER_ACCOUNT_DEACTIVATED} by
 * user-service when a user deactivates their account (sets
 * {@code isActive = false}).
 *
 * <p>Consumed by: notification-service (sends a deactivation confirmation
 * email); potentially cart-service later (to clear the user's Redis cart,
 * per the locked account-deactivation flow).
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class AccountDeactivatedEvent extends KafkaEvent {

    /**
     * ID of the deactivated user account.
     */
    private Long userId;

    /**
     * Email address to notify.
     */
    private String email;
}