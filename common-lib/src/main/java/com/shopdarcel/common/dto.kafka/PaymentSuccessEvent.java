package com.shopdarcel.common.dto.kafka;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Published to {@code KafkaTopics.PAYMENT_SUCCESS} when payment processing
 * for an order succeeds.
 *
 * <p>Consumed by: order-service (advances the saga toward confirmation
 * alongside {@link InventoryReservedEvent}).
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class PaymentSuccessEvent extends KafkaEvent {

    /**
     * ID of the order the payment was for.
     */
    private Long orderId;

    /**
     * ID of the user who made the payment.
     */
    private Long userId;

    /**
     * Amount successfully charged.
     */
    private BigDecimal amount;

    /**
     * ID of the payment transaction from the payment processor.
     */
    private String paymentId;

    /**
     * Payment method used, e.g. "CARD", "WALLET".
     */
    private String paymentMethod;
}