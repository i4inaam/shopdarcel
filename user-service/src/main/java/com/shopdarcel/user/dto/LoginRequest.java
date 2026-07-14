package com.shopdarcel.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Incoming request payload for user login.
 * <p>
 * Deliberately minimal validation here (just presence checks) — we don't
 * re-validate password format/complexity at login, since that's the
 * concern of registration only. A login request should simply attempt
 * to authenticate whatever is provided.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}