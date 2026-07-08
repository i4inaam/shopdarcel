package com.shopdarcel.common.dto.kafka;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

/**
 * Published to {@code KafkaTopics.CART_CHECKOUT_INITIATED} by cart-service
 * when a user initiates checkout from their cart.
 *
 * <p>Consumed by: order-service (creates the order and starts the saga by
 * publishing {@link OrderPlacedEvent}).
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class CartCheckoutInitiatedEvent extends KafkaEvent {

    /**
     * ID of the cart being checked out.
     */
    private Long cartId;

    /**
     * ID of the user checking out.
     */
    private Long userId;

    /**
     * Snapshot of the cart's line items at checkout time.
     */
    private List<OrderItemEvent> items;

    /**
     * Total cart amount at checkout time.
     */
    private BigDecimal totalAmount;
}