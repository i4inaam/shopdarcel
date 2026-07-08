package com.shopdarcel.common.dto.kafka;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Published to {@code KafkaTopics.ORDER_CANCELLED} by order-service when
 * the saga is compensated — e.g. inventory reservation or payment failed,
 * so the order must be rolled back.
 *
 * <p>Consumed by: inventory-service (releases any reserved stock),
 * notification-service (sends an order-cancelled notice).
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class OrderCancelledEvent extends KafkaEvent {

    /**
     * ID of the cancelled order.
     */
    private Long orderId;

    /**
     * ID of the user who owns the order.
     */
    private Long userId;

    /**
     * Why the order was cancelled, e.g. "INVENTORY_UNAVAILABLE", "PAYMENT_FAILED".
     */
    private String reason;
}