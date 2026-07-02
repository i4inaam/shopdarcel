package com.shopdarcel.common.dto.kafka;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Published to {@code KafkaTopics.ORDER_CONFIRMED} by order-service once
 * every saga participant (inventory reservation, payment) has succeeded
 * for a given order.
 *
 * <p>Consumed by: notification-service (sends an order-confirmed email/SMS).
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class OrderConfirmedEvent extends KafkaEvent {

    /**
     * ID of the confirmed order.
     */
    private Long orderId;

    /**
     * ID of the user who owns the order.
     */
    private Long userId;
}