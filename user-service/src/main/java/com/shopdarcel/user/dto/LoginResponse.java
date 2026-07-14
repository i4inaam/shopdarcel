package com.shopdarcel.user.dto;

import lombok.*;

/**
 * Response payload returned after a successful login, containing both
 * the short-lived access token (for API calls) and the longer-lived
 * refresh token (for obtaining new access tokens without re-authenticating).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String accessToken;
    private String refreshToken;

    /**
     * Token type, per OAuth2/JWT convention — always "Bearer" for our tokens.
     */
    @Builder.Default
    private String tokenType = "Bearer";

    private UserResponse user;
}