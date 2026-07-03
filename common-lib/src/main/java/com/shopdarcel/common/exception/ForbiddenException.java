package com.shopdarcel.common.exception;

/**
 * Thrown when the caller is authenticated but lacks permission for the
 * requested action — e.g. a CUSTOMER attempting an ADMIN-only operation,
 * or a user attempting to access another user's order.
 *
 * <p>Maps to HTTP 403 Forbidden / error code "FORBIDDEN".
 */
public class ForbiddenException extends ApiException {

    private static final long serialVersionUID = 1L;

    private static final int STATUS = 403;
    private static final String ERROR_CODE = "FORBIDDEN";

    public ForbiddenException(String message) {
        super(STATUS, ERROR_CODE, message);
    }
}