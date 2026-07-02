package com.shopdarcel.common.exception;

import lombok.Getter;

/**
 * Base type for every exception in the shared exception hierarchy.
 *
 * <p>Each concrete subclass fixes an HTTP status code and a short,
 * machine-readable {@code errorCode} (e.g. {@code "NOT_FOUND"}), matching
 * the {@code error} field documented for {@code ApiError} in
 * ARCHITECTURE.md. Every service's {@code GlobalExceptionHandler}
 * (a {@code @ControllerAdvice}) catches {@link ApiException} once and maps
 * it generically to an {@code ApiError} response — services should never
 * need a bespoke {@code @ExceptionHandler} per exception subtype.
 *
 * <p>This is intentionally an unchecked exception ({@code RuntimeException})
 * so business/service-layer code isn't forced to declare
 * {@code throws} clauses or wrap every call site in try/catch.
 */
@Getter
public abstract class ApiException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * HTTP status code this exception should be translated to (e.g. 404, 409).
     */
    private final int status;

    /**
     * Short, machine-readable error code (e.g. "NOT_FOUND", "CONFLICT"). Matches ApiError.error.
     */
    private final String errorCode;

    protected ApiException(int status, String errorCode, String message) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    protected ApiException(int status, String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.errorCode = errorCode;
    }
}