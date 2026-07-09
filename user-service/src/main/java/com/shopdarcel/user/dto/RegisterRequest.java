package com.shopdarcel.user.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

/**
 * Incoming request payload for user registration.
 * <p>
 * Validation is enforced via Jakarta Bean Validation annotations and
 * triggered by {@code @Valid} on the controller method parameter —
 * invalid requests never reach the service layer.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$", message = "Min 8 characters, 1 uppercase, 1 lowercase, 1 number")
    private String password;

    @NotBlank(message = "First name is required")
    private String firstName;

    /**
     * Optional — matches {@link com.shopdarcel.user.entity.User#getLastName()}.
     */
    private String lastName;

    /**
     * Must be {@code true} for registration to succeed — users must
     * explicitly accept the platform's terms and conditions. The exact
     * acceptance timestamp is recorded server-side, not taken from the client.
     */
    @AssertTrue(message = "You must accept the terms and conditions")
    private boolean termsAccepted;
}