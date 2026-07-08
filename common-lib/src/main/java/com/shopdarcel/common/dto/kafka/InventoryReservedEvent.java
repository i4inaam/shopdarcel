package com.shopdarcel.common.dto.kafka;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Published to {@code KafkaTopics.INVENTORY_RESERVED} by inventory-service
 * once stock has been successfully reserved for every line item in an order.
 *
 * <p>Consumed by: order-service (advances the saga toward confirmation).
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class InventoryReservedEvent extends KafkaEvent {

    /**
     * ID of the order this reservation is for.
     */
    private Long orderId;

    /**
     * Line items successfully reserved.
     */
    private List<OrderItemEvent> items;
}