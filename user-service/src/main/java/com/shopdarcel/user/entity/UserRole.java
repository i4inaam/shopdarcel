package com.shopdarcel.user.entity;

/**
 * Represents the authorization role assigned to a {@link User}.
 * <p>
 * Persisted as a {@code String} (via {@code @Enumerated(EnumType.STRING)}
 * on the owning entity field) rather than by ordinal position, so that
 * adding or reordering roles in the future never corrupts existing data.
 */
public enum UserRole {

    /**
     * Standard authenticated customer with no elevated privileges.
     */
    ROLE_USER,

    /**
     * Administrative user with elevated privileges across the platform.
     */
    ROLE_ADMIN
}