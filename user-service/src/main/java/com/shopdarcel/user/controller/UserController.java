package com.shopdarcel.user.controller;

import com.shopdarcel.user.constants.AuthMessages;
import com.shopdarcel.user.dto.*;
import com.shopdarcel.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    /**
     * Changes the currently authenticated user's password.
     *
     * @param userId  the raw {@code X-User-Id} header value
     * @param request current and new password
     * @return HTTP 204 No Content on success
     */
    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(userId, request);
        return ResponseEntity.noContent()
                .build();
    }

    /**
     * Updates the currently authenticated user's profile. Only non-null
     * fields in the request body are applied.
     *
     * @param userId  the raw {@code X-User-Id} header value
     * @param request fields to update
     * @return the updated profile, with HTTP 200 OK
     */
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody UpdateProfileRequest request) {
        UserResponse response = userService.updateProfile(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Initiates a password reset for the given email, if an account exists.
     * <p>
     * Always returns the same generic success response regardless of whether
     * the email is registered, to avoid revealing which emails exist in the
     * system. The actual reset link is delivered by notification-service via
     * the {@code user.password.reset.requested} Kafka event.
     *
     * @param request the email to send a reset link to
     * @return HTTP 200 OK with a generic message, always
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        userService.forgotPassword(request);
        return ResponseEntity.ok(Map.of("message", AuthMessages.PASSWORD_RESET_GENERIC_SUCCESS));
    }

    /**
     * Completes a password reset using a valid, unexpired reset token.
     * <p>
     * On success, clears any existing account lockout, since successfully
     * presenting the reset token proves ownership of the account.
     *
     * @param request the reset token and new password
     * @return HTTP 200 OK with a confirmation message
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request);
        return ResponseEntity.ok(Map.of("message", AuthMessages.PASSWORD_RESET_SUCCESS));
    }

    /**
     * Verifies a user's email using a valid, unexpired verification token.
     *
     * @param request the verification token
     * @return HTTP 200 OK with a confirmation message
     */
    @PostMapping("/verify-email")
    public ResponseEntity<Map<String, String>> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest request) {
        userService.verifyEmail(request);
        return ResponseEntity.ok(Map.of("message", AuthMessages.EMAIL_VERIFICATION_SUCCESS));
    }

    /**
     * Resends a verification email, if an account exists for this email and
     * isn't already verified. Always returns the same generic response.
     *
     * @param request the email to resend verification to
     * @return HTTP 200 OK with a generic message, always
     */
    @PostMapping("/resend-verification")
    public ResponseEntity<Map<String, String>> resendVerification(
            @Valid @RequestBody ResendVerificationRequest request) {
        userService.resendVerification(request);
        return ResponseEntity.ok(Map.of("message", AuthMessages.VERIFICATION_EMAIL_SENT));
    }

    /**
     * Deactivates the currently authenticated user's account.
     *
     * @param userId the raw {@code X-User-Id} header value
     * @return HTTP 200 OK with a confirmation message
     */
    @PostMapping("/me/deactivate")
    public ResponseEntity<Map<String, String>> deactivateAccount(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        userService.deactivateAccount(userId);
        return ResponseEntity.ok(Map.of("message", AuthMessages.ACCOUNT_DEACTIVATION_SUCCESS));
    }

    /**
     * Reactivates a deactivated account using email and password, since
     * normal login blocks deactivated accounts before checking credentials.
     *
     * @param request email and password
     * @return tokens and profile info, with HTTP 200 OK
     */
    @PostMapping("/reactivate")
    public ResponseEntity<LoginResponse> reactivateAccount(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.reactivateAccount(request);
        return ResponseEntity.ok(response);
    }
}