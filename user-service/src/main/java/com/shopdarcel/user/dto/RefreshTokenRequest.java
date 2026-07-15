package com.shopdarcel.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Incoming request payload to exchange a valid refresh token for a new
 * access token, without requiring the user to re-authenticate.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}