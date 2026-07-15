package com.shopdarcel.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

/**
 * Incoming request payload to change the currently authenticated user's
 * password. Requires the current password as proof of identity, since
 * this endpoint is reachable with just a valid access token — no
 * additional re-authentication step.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordRequest {

    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$", message = "Password must contain at least 8 characters, including one uppercase letter, one lowercase letter, and one number")
    private String newPassword;
}