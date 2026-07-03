package com.shopdarcel.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Standard success response envelope wrapping every non-paginated,
 * non-error response body returned by ShopDarcel services.
 *
 * @param <T> type of the payload carried in {@code data}
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /**
     * Always {@code true} for this envelope — errors use {@link ApiError} instead.
     */
    @Builder.Default
    private boolean success = true;

    /**
     * Optional human-readable message, e.g. "Product created successfully".
     */
    private String message;

    /**
     * The actual response payload.
     */
    private T data;

    /**
     * UTC instant the response was generated.
     */
    @Builder.Default
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant timestamp = Instant.now();

    /**
     * Shorthand for a successful response carrying only a payload, no message.
     */
    public static <T> ApiResponse<T> of(T data) {
        return ApiResponse.<T>builder().data(data).build();
    }

    /**
     * Shorthand for a successful response carrying a payload and a message.
     */
    public static <T> ApiResponse<T> of(T data, String message) {
        return ApiResponse.<T>builder().data(data).message(message).build();
    }
}