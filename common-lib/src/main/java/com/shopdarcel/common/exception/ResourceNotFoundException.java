package com.shopdarcel.common.exception;

/**
 * Thrown when a requested resource does not exist — e.g. a product ID,
 * order ID, or user ID that has no matching row.
 *
 * <p>Maps to HTTP 404 Not Found / error code "NOT_FOUND".
 */
public class ResourceNotFoundException extends ApiException {

    private static final long serialVersionUID = 1L;

    private static final int STATUS = 404;
    private static final String ERROR_CODE = "NOT_FOUND";

    public ResourceNotFoundException(String message) {
        super(STATUS, ERROR_CODE, message);
    }

    /**
     * Convenience constructor for the extremely common
     * "{@code <Entity> with id <id> not found}" case, keeping call sites
     * terse and message wording consistent across services.
     */
    public ResourceNotFoundException(String entityName, Object id) {
        super(STATUS, ERROR_CODE, String.format("%s with id %s not found", entityName, id));
    }
}