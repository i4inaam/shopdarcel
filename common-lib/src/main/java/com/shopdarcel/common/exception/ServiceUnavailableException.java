package com.shopdarcel.common.exception;

/**
 * Thrown when a dependency this service needs is temporarily unreachable
 * or unhealthy — e.g. Kafka broker unreachable, Redis connection pool
 * exhausted, or a downstream saga participant timing out.
 *
 * <p>Maps to HTTP 503 Service Unavailable / error code "SERVICE_UNAVAILABLE".
 */
public class ServiceUnavailableException extends ApiException {

    private static final long serialVersionUID = 1L;

    private static final int STATUS = 503;
    private static final String ERROR_CODE = "SERVICE_UNAVAILABLE";

    public ServiceUnavailableException(String message) {
        super(STATUS, ERROR_CODE, message);
    }

    public ServiceUnavailableException(String message, Throwable cause) {
        super(STATUS, ERROR_CODE, message, cause);
    }
}