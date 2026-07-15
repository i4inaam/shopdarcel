package com.shopdarcel.user.service;

import com.shopdarcel.user.dto.*;

/**
 * Business logic contract for user account operations.
 */
public interface UserService {

    /**
     * Registers a new user account.
     * <p>
     * Hashes the raw password, stamps server-side audit timestamps
     * ({@code termsAcceptedAt}, {@code passwordChangedAt}), assigns the
     * default {@code ROLE_USER} role, and persists the new user.
     *
     * @param request validated registration details from the client
     * @return the created user, mapped to its public-facing response shape
     * @throws com.shopdarcel.common.exception.ConflictException if the email is already registered
     */
    UserResponse register(RegisterRequest request);

    /**
     * Authenticates a user and issues access/refresh tokens.
     * <p>
     * Tracks failed login attempts for brute-force protection: after 5
     * consecutive failures, the account is locked via {@code accountLockedAt}.
     *
     * @param request login credentials
     * @return tokens and basic user profile info
     * @throws com.shopdarcel.common.exception.UnauthorizedException if credentials are invalid
     * @throws com.shopdarcel.common.exception.ForbiddenException    if the account is locked or deactivated
     */
    LoginResponse login(LoginRequest request);

    /**
     * Exchanges a valid refresh token for a new access token (and a new,
     * rotated refresh token). The user's current role is re-fetched from the
     * database rather than trusted from the old token, in case it changed
     * since the refresh token was issued.
     *
     * @param request contains the refresh token to validate
     * @return a new token pair and basic user profile info
     * @throws com.shopdarcel.common.exception.UnauthorizedException if the token is invalid, expired, or the user no longer exists
     */
    LoginResponse refreshToken(RefreshTokenRequest request);

    /**
     * Fetches the profile of the currently authenticated user.
     * <p>
     * Accepts the raw {@code X-User-Id} header value as a string so that
     * both its absence and any malformed (non-numeric) value can be
     * validated in one place, alongside the existence check.
     *
     * @param userIdHeader the raw value of the {@code X-User-Id} header, normally set by api-gateway after JWT validation
     * @return the user's public-facing profile
     * @throws com.shopdarcel.common.exception.UnauthorizedException     if the header is missing, blank, or not a valid numeric ID
     * @throws com.shopdarcel.common.exception.ResourceNotFoundException if no user exists with this ID
     */
    UserResponse getCurrentUser(String userIdHeader);
}