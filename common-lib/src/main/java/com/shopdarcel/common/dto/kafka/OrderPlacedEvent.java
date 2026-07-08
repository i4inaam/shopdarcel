package com.shopdarcel.common.dto.kafka;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

/**
 * Published to {@code KafkaTopics.ORDER_PLACED} by order-service once an
 * order has been persisted and the choreography saga has begun.
 *
 * <p>Consumed by: inventory-service (reserves stock for each line item),
 * notification-service (sends an order-received confirmation).
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class OrderPlacedEvent extends KafkaEvent {

    /**
     * ID of the newly placed order.
     */
    private Long orderId;

    /**
     * ID of the user who placed the order.
     */
    private Long userId;

    /**
     * Line items included in the order.
     */
    private List<OrderItemEvent> items;

    /**
     * Total order amount, including all line items.
     */
    private BigDecimal totalAmount;

    /**
     * Shipping address as a formatted string snapshot at order time.
     */
    private String shippingAddress;
}