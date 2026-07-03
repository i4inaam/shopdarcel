package com.shopdarcel.common.exception;

import com.shopdarcel.common.response.ApiError;

import java.util.Collections;
import java.util.List;

/**
 * Thrown for service-layer validation failures that bean-validation
 * annotations can't express — e.g. cross-field checks, or checks that
 * require a database lookup. Ordinary {@code @Valid @RequestBody}
 * failures are raised by Spring itself as
 * {@code MethodArgumentNotValidException} and handled directly by each
 * service's {@code GlobalExceptionHandler} — this class is not involved
 * in that path.
 *
 * <p>Maps to HTTP 400 Bad Request / error code "VALIDATION_FAILED".
 */
public class ValidationException extends ApiException {

    private static final long serialVersionUID = 1L;

    private static final int STATUS = 400;
    private static final String ERROR_CODE = "VALIDATION_FAILED";

    private final List<ApiError.FieldError> fieldErrors;

    public ValidationException(String message) {
        super(STATUS, ERROR_CODE, message);
        this.fieldErrors = Collections.emptyList();
    }

    public ValidationException(String message, List<ApiError.FieldError> fieldErrors) {
        super(STATUS, ERROR_CODE, message);
        this.fieldErrors = fieldErrors == null ? Collections.emptyList() : List.copyOf(fieldErrors);
    }

    /**
     * Field-level validation failures, if any were supplied. Empty (never null) otherwise.
     */
    public List<ApiError.FieldError> getFieldErrors() {
        return fieldErrors;
    }
}