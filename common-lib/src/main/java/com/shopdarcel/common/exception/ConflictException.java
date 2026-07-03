package com.shopdarcel.common.exception;

/**
 * Thrown when a request conflicts with the current state of the resource —
 * e.g. registering an email that already exists, or double-confirming an
 * already-confirmed order.
 *
 * <p>Maps to HTTP 409 Conflict / error code "CONFLICT".
 */
public class ConflictException extends ApiException {

    private static final long serialVersionUID = 1L;

    private static final int STATUS = 409;
    private static final String ERROR_CODE = "CONFLICT";

    public ConflictException(String message) {
        super(STATUS, ERROR_CODE, message);
    }
}