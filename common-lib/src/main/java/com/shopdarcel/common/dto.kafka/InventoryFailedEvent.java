package com.shopdarcel.common.dto.kafka;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Published to {@code KafkaTopics.INVENTORY_FAILED} by inventory-service
 * when stock could not be reserved for one or more line items — triggers
 * saga compensation.
 *
 * <p>Consumed by: order-service (cancels the order and publishes
 * {@link OrderCancelledEvent}).
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class InventoryFailedEvent extends KafkaEvent {

    /**
     * ID of the order whose reservation failed.
     */
    private Long orderId;

    /**
     * Why the reservation failed, e.g. "INSUFFICIENT_STOCK".
     */
    private String reason;

    /**
     * The specific line items that could not be reserved.
     */
    private List<OrderItemEvent> failedItems;
}