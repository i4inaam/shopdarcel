package com.shopdarcel.user.entity;

import com.shopdarcel.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Represents a registered user of the ShopDarcel platform.
 * <p>
 * Extends {@link BaseEntity} for {@code id}, {@code createdAt}, and
 * {@code updatedAt}. Follows the platform-wide soft-delete convention:
 * users are never hard-deleted, only deactivated via {@link #isActive}.
 * <p>
 * Security-related fields ({@link #failedLoginAttempts}, {@link #accountLockedAt})
 * support brute-force protection at the service layer: an account is locked
 * after 5 consecutive failed login attempts.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, of = {})
public class User extends BaseEntity {

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /**
     * BCrypt-hashed password. Never stores or logs the raw password.
     */
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    /**
     * Optional — not every user provides a last name at registration.
     */
    @Column(name = "last_name")
    private String lastName;

    /**
     * Soft-delete flag. {@code false} means the account is deactivated —
     * never hard-deleted, per platform-wide soft-delete convention.
     * Defaults to {@code true} at registration.
     */
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    /**
     * Defaults to {@code false} until the user confirms their email.
     */
    @Builder.Default
    @Column(name = "is_email_verified", nullable = false)
    private boolean isEmailVerified = false;

    /**
     * Exact timestamp the user accepted the platform's terms and conditions, for legal/audit purposes.
     */
    @Column(name = "terms_accepted_at", nullable = false)
    private Instant termsAcceptedAt;

    /**
     * Updated on every successful login. Null until the user's first login.
     */
    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    /**
     * Consecutive failed login attempts since the last successful login
     * or account unlock. Reset to 0 on successful authentication.
     * Used for brute-force protection: the account locks after 5 failures.
     */
    @Builder.Default
    @Column(name = "failed_login_attempts", nullable = false)
    private int failedLoginAttempts = 0;

    /**
     * Timestamp the account was locked due to exceeding the failed-login
     * threshold. Null when the account is not locked.
     */
    @Column(name = "account_locked_at")
    private Instant accountLockedAt;

    /**
     * Updated whenever the user changes their password, for security auditing.
     */
    @Column(name = "password_changed_at", nullable = false)
    private Instant passwordChangedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;
}