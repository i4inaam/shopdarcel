package com.shopdarcel.common.dto.kafka;

import lombok.*;

import java.math.BigDecimal;

/**
 * A single product line item, embedded inside the Kafka events that carry
 * a list of products — {@link OrderPlacedEvent}, {@link CartCheckoutInitiatedEvent},
 * {@link InventoryReservedEvent}, and {@link InventoryFailedEvent}.
 *
 * <p>This is intentionally a flat, denormalized snapshot (product ID,
 * quantity, unit price at the time of the event) rather than a reference
 * that requires a follow-up lookup — Kafka event consumers should never
 * need to call another service to interpret an event's payload, since
 * service-to-service communication is Kafka-only with zero synchronous
 * REST calls between services.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemEvent {

    /**
     * Product ID this line item refers to.
     */
    private Long productId;

    /**
     * Quantity of this product.
     */
    private Integer quantity;

    /**
     * Unit price at the time the event was produced (snapshot, not a live lookup).
     */
    private BigDecimal unitPrice;
}