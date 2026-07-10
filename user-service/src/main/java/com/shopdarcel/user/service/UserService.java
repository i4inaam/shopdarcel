package com.shopdarcel.user.service;

import com.shopdarcel.user.dto.RegisterRequest;
import com.shopdarcel.user.dto.UserResponse;

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
}