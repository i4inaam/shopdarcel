package com.shopdarcel.common.constants;

/**
 * Application-wide constants shared by every ShopDarcel service.
 *
 * <p>Anything referenced by more than one service — HTTP header names,
 * pagination defaults, date/time formats — belongs here. Constants used by
 * only a single service belong in that service's own
 * {@code constants/} package instead (see ARCHITECTURE.md — "Constants Rule").
 */
public final class AppConstants {

    private AppConstants() {
        // Constants holder — never instantiated.
    }

    // ---------------------------------------------------------------
    // Security / identity propagation headers
    //
    // JWT is validated ONLY at api-gateway. The gateway extracts the
    // token claims and forwards them to internal services as plain HTTP
    // headers over the (network-isolated) internal Docker/EC2 network.
    // Internal services trust these headers implicitly but MUST reject
    // any request where X_USER_ID is missing, as a defense-in-depth
    // check against someone bypassing the gateway.
    // ---------------------------------------------------------------

    /**
     * Forwarded by api-gateway; the authenticated user's ID.
     */
    public static final String HEADER_USER_ID = "X-User-Id";

    /**
     * Forwarded by api-gateway; the authenticated user's role (e.g. CUSTOMER, ADMIN).
     */
    public static final String HEADER_USER_ROLE = "X-User-Role";

    /**
     * Present on every request/response for distributed tracing across services and Kafka events.
     */
    public static final String HEADER_CORRELATION_ID = "X-Correlation-Id";

    // ---------------------------------------------------------------
    // Pagination defaults
    // ---------------------------------------------------------------

    /**
     * Default page index (0-based) when a paginated request omits one.
     */
    public static final int DEFAULT_PAGE_NUMBER = 0;

    /**
     * Default page size when a paginated request omits one.
     */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * Hard ceiling on page size, regardless of what the caller requests.
     */
    public static final int MAX_PAGE_SIZE = 100;

    /**
     * Default sort field for paginated list endpoints that don't specify one.
     */
    public static final String DEFAULT_SORT_FIELD = "createdAt";

    /**
     * Default sort direction for paginated list endpoints that don't specify one.
     */
    public static final String DEFAULT_SORT_DIRECTION = "DESC";

    // ---------------------------------------------------------------
    // Formatting
    // ---------------------------------------------------------------

    /**
     * ISO-8601 timestamp format used consistently across all API responses and logs.
     */
    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    // ---------------------------------------------------------------
    // Roles
    //
    // Kept here (rather than per-service) because the role value is
    // written by user-service but read/enforced by every other service
    // via the X-User-Role header.
    // ---------------------------------------------------------------

    public static final String ROLE_CUSTOMER = "CUSTOMER";
    public static final String ROLE_ADMIN = "ADMIN";
}