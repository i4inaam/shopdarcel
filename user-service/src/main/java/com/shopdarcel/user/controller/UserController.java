package com.shopdarcel.user.controller;

import com.shopdarcel.user.dto.*;
import com.shopdarcel.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST endpoints for user account operations.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Registers a new user account.
     *
     * @param request validated registration payload
     * @return the created user, with HTTP 201 Created
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Authenticates a user and issues access/refresh tokens.
     *
     * @param request login credentials
     * @return tokens and basic user profile info, with HTTP 200 OK
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Exchanges a valid refresh token for a new access/refresh token pair.
     *
     * @param request contains the refresh token
     * @return new tokens and basic user profile info, with HTTP 200 OK
     */
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        LoginResponse response = userService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Returns the profile of the currently authenticated user.
     * <p>
     * The {@code X-User-Id} header is normally set by api-gateway after JWT
     * validation. All validation of this header (presence, format) is
     * delegated to the service layer, per ARCHITECTURE.md's security
     * architecture — internal services trust but verify the forwarded header.
     *
     * @param userId the raw {@code X-User-Id} header value
     * @return the user's profile, with HTTP 200 OK
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UserResponse response = userService.getCurrentUser(userId);
        return ResponseEntity.ok(response);
    }
}