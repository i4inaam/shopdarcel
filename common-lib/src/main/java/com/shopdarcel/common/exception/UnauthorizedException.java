package com.shopdarcel.common.exception;

/**
 * Thrown when a request has no valid authentication — e.g. missing or
 * invalid JWT, expired token, or (internally) a request arriving at a
 * downstream service without the {@code X-User-Id} header that
 * api-gateway is expected to forward.
 *
 * <p>Maps to HTTP 401 Unauthorized / error code "UNAUTHORIZED".
 */
public class UnauthorizedException extends ApiException {

    private static final long serialVersionUID = 1L;

    private static final int STATUS = 401;
    private static final String ERROR_CODE = "UNAUTHORIZED";

    public UnauthorizedException(String message) {
        super(STATUS, ERROR_CODE, message);
    }
}