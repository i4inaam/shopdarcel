package com.shopdarcel.common.constants;

/**
 * Centralized registry of every Kafka topic name used across ShopDarcel.
 *
 * <p>This is the single source of truth for topic names. A service should
 * never hardcode a topic name string in a {@code @KafkaListener(topics = "...")}
 * annotation or a {@code KafkaTemplate.send(...)} call — always reference a
 * constant from this class. This guarantees producers and consumers can
 * never drift apart on topic naming (e.g. "order.placed" vs "order-placed").
 *
 * <p>Naming convention: {@code <domain>.<event>} in dot-separated,
 * past-tense form, mirroring the choreography saga event flow documented in
 * ARCHITECTURE.md.
 */
public final class KafkaTopics {

    private KafkaTopics() {
        // Constants holder — never instantiated.
    }

    /**
     * Published by user-service after a new account is successfully created.
     */
    public static final String USER_REGISTERED = "user.registered";

    /**
     * Published by order-service once an order has been persisted and the saga has started.
     */
    public static final String ORDER_PLACED = "order.placed";

    /**
     * Published by order-service once all saga participants have confirmed the order.
     */
    public static final String ORDER_CONFIRMED = "order.confirmed";

    /**
     * Published by order-service when the saga is compensated and the order is rolled back.
     */
    public static final String ORDER_CANCELLED = "order.cancelled";

    /**
     * Published by inventory-service when stock has been successfully reserved for an order.
     */
    public static final String INVENTORY_RESERVED = "inventory.reserved";

    /**
     * Published by inventory-service when stock reservation fails (triggers saga compensation).
     */
    public static final String INVENTORY_FAILED = "inventory.failed";

    /**
     * Published by cart-service when a user initiates checkout from their cart.
     */
    public static final String CART_CHECKOUT_INITIATED = "cart.checkout.initiated";

    /**
     * Published when payment processing for an order succeeds.
     */
    public static final String PAYMENT_SUCCESS = "payment.success";
}