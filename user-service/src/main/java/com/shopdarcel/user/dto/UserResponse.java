package com.shopdarcel.user.dto;

import com.shopdarcel.user.entity.UserRole;
import lombok.*;

import java.time.Instant;

/**
 * Outgoing response payload representing a user.
 * <p>
 * Deliberately excludes sensitive fields such as {@code password},
 * {@code failedLoginAttempts}, and {@code accountLockedAt} — this DTO
 * defines the public-facing shape of a user, decoupled from the
 * persistence model in {@link com.shopdarcel.user.entity.User}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private boolean active;
    private boolean emailVerified;
    private UserRole role;
    private Instant createdAt;
}