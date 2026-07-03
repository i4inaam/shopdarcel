package com.shopdarcel.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Standard error response shape returned by every ShopDarcel service.
 * Every {@code GlobalExceptionHandler} (@ControllerAdvice) across all
 * seven services builds and returns this exact shape, so API consumers
 * only ever have to parse one error format regardless of which service
 * answered the request.
 *
 * <p>For a bean-validation failure on a {@code @Valid @RequestBody}, the
 * {@code errors} field is populated with one {@link FieldError} per
 * invalid field.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    /**
     * HTTP status code, e.g. 404, 400, 409.
     */
    private int status;

    /**
     * Short, machine-readable error code, e.g. "NOT_FOUND", "VALIDATION_FAILED".
     */
    private String error;

    /**
     * Human-readable error message.
     */
    private String message;

    /**
     * UTC instant the error occurred.
     */
    @Builder.Default
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant timestamp = Instant.now();

    /**
     * Request path that produced the error, e.g. "/api/products/99".
     */
    private String path;

    /**
     * Populated only for multi-field validation failures; null otherwise.
     */
    private List<FieldError> errors;

    /**
     * A single field-level validation failure, used inside {@link ApiError#errors}
     * when a request body fails bean validation on more than one field.
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FieldError {

        /**
         * Name of the invalid field, e.g. "email".
         */
        private String field;

        /**
         * The value that was rejected, e.g. "notanemail". May be null if the value itself was null.
         */
        private Object rejectedValue;

        /**
         * Human-readable reason the value was rejected.
         */
        private String message;
    }
}