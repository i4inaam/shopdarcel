package com.shopdarcel.common.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

    @Test
    void of_withDataOnly_defaultsSuccessTrueAndSetsTimestamp() {
        ApiResponse<String> response = ApiResponse.of("payload");

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isEqualTo("payload");
        assertThat(response.getMessage()).isNull();
        assertThat(response.getTimestamp()).isNotNull();
    }

    @Test
    void of_withDataAndMessage_setsBothFields() {
        ApiResponse<Integer> response = ApiResponse.of(42, "Created successfully");

        assertThat(response.getData()).isEqualTo(42);
        assertThat(response.getMessage()).isEqualTo("Created successfully");
        assertThat(response.isSuccess()).isTrue();
    }

    @Test
    void builder_allowsOverridingSuccessToFalse() {
        ApiResponse<String> response = ApiResponse
                .<String>builder()
                .success(false)
                .message("Something odd, but not an error")
                .build();

        assertThat(response.isSuccess()).isFalse();
    }
}