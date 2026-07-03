package com.shopdarcel.common.exception;

/**
 * Thrown for generic malformed-request cases that don't fit
 * {@link ValidationException}'s field-level shape — e.g. an unparseable
 * query parameter, an invalid enum value, or a structurally malformed
 * request body.
 *
 * <p>Maps to HTTP 400 Bad Request / error code "BAD_REQUEST".
 */
public class BadRequestException extends ApiException {

    private static final long serialVersionUID = 1L;

    private static final int STATUS = 400;
    private static final String ERROR_CODE = "BAD_REQUEST";

    public BadRequestException(String message) {
        super(STATUS, ERROR_CODE, message);
    }
}