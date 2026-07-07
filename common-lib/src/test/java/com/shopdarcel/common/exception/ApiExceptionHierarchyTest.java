package com.shopdarcel.common.exception;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ApiExceptionHierarchyTest {


    @Test
    void resourceNotFoundException_plainMessageConstructor_usesMessageAsIs() {
        var ex = new ResourceNotFoundException("No account found for this token");

        assertThat(ex.getStatus()).isEqualTo(404);
        assertThat(ex.getErrorCode()).isEqualTo("NOT_FOUND");
        assertThat(ex.getMessage()).isEqualTo("No account found for this token");
    }

    @Test
    void resourceNotFoundException_convenienceConstructor_buildsExpectedMessage() {
        var ex = new ResourceNotFoundException("Product", 99);

        assertThat(ex.getStatus()).isEqualTo(404);
        assertThat(ex.getErrorCode()).isEqualTo("NOT_FOUND");
        assertThat(ex.getMessage()).isEqualTo("Product with id 99 not found");
    }

    @Test
    void validationException_withoutFieldErrors_returnsEmptyListNotNull() {
        var ex = new ValidationException("Validation failed");

        assertThat(ex.getStatus()).isEqualTo(400);
        assertThat(ex.getErrorCode()).isEqualTo("VALIDATION_FAILED");
        assertThat(ex.getFieldErrors()).isEmpty();
    }

    @Test
    void validationException_withFieldErrors_returnsThemUnmodified() {
        var fieldError = com.shopdarcel.common.response.ApiError.FieldError.builder()
                .field("email")
                .rejectedValue("notanemail")
                .message("Must be a valid email address")
                .build();

        var ex = new ValidationException("Validation failed", List.of(fieldError));

        assertThat(ex.getFieldErrors()).hasSize(1);
        assertThat(ex.getFieldErrors()
                .get(0)
                .getField()).isEqualTo("email");
        assertThat(ex.getFieldErrors()
                .get(0)
                .getRejectedValue()).isEqualTo("notanemail");
    }

    @Test
    void serviceUnavailableException_withCause_preservesOriginalException() {
        var originalCause = new RuntimeException("Kafka broker unreachable");
        var ex = new ServiceUnavailableException("Failed to publish event", originalCause);

        assertThat(ex.getStatus()).isEqualTo(503);
        assertThat(ex.getMessage()).isEqualTo("Failed to publish event");
        assertThat(ex.getCause()).isSameAs(originalCause);
    }

    @Test
    void everyRemainingSubclass_hasCorrectStatusAndErrorCode() {
        assertThat(new ConflictException("x").getStatus()).isEqualTo(409);
        assertThat(new ConflictException("x").getErrorCode()).isEqualTo("CONFLICT");

        assertThat(new UnauthorizedException("x").getStatus()).isEqualTo(401);
        assertThat(new UnauthorizedException("x").getErrorCode()).isEqualTo("UNAUTHORIZED");

        assertThat(new ForbiddenException("x").getStatus()).isEqualTo(403);
        assertThat(new ForbiddenException("x").getErrorCode()).isEqualTo("FORBIDDEN");

        assertThat(new BadRequestException("x").getStatus()).isEqualTo(400);
        assertThat(new BadRequestException("x").getErrorCode()).isEqualTo("BAD_REQUEST");

        assertThat(new ServiceUnavailableException("x").getStatus()).isEqualTo(503);
        assertThat(new ServiceUnavailableException("x").getErrorCode()).isEqualTo("SERVICE_UNAVAILABLE");
    }
}