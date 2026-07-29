package com.shopdarcel.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopdarcel.common.exception.ConflictException;
import com.shopdarcel.common.exception.UnauthorizedException;
import com.shopdarcel.user.config.SecurityFilterConfig;
import com.shopdarcel.user.dto.user.*;
import com.shopdarcel.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Controller slice tests for {@link UserController}, verifying HTTP-level
 * concerns (status codes, JSON shape, validation triggering) with the
 * service layer mocked — no database, no Kafka, no real business logic.
 */
@WebMvcTest(UserController.class)
@Import(SecurityFilterConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void register_withValidRequest_returns201() throws Exception {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .email("test@example.com")
                .password("Password1!")
                .firstName("Inaam")
                .lastName("Haq")
                .termsAccepted(true)
                .build();

        UserResponse response = UserResponse.builder()
                .id(1L)
                .email("test@example.com")
                .build();

        when(userService.register(any(RegisterRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/users/register").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void register_withInvalidEmail_returns400() throws Exception {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .email("not-an-email")
                .password("Password1!")
                .firstName("Inaam")
                .termsAccepted(true)
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/users/register").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"));
    }

    @Test
    void register_withDuplicateEmail_returns409() throws Exception {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .email("test@example.com")
                .password("Password1!")
                .firstName("Inaam")
                .termsAccepted(true)
                .build();

        when(userService.register(any(RegisterRequest.class))).thenThrow(new ConflictException("Email is already registered"));

        // Act & Assert
        mockMvc.perform(post("/api/users/register").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("CONFLICT"));
    }

    @Test
    void login_withValidCredentials_returns200() throws Exception {
        // Arrange
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("Password1!")
                .build();

        LoginResponse response = LoginResponse.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .build();

        when(userService.login(any(LoginRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/users/login").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"));
    }

    @Test
    void login_withInvalidCredentials_returns401() throws Exception {
        // Arrange
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("WrongPassword1!")
                .build();

        when(userService.login(any(LoginRequest.class))).thenThrow(new UnauthorizedException("Invalid email or password"));

        // Act & Assert
        mockMvc.perform(post("/api/users/login").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    void getCurrentUser_withValidHeader_returns200() throws Exception {
        // Arrange
        UserResponse response = UserResponse.builder()
                .id(1L)
                .email("test@example.com")
                .build();
        when(userService.getCurrentUser("1")).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/users/me").header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getCurrentUser_withMissingHeader_returns401() throws Exception {
        // Arrange
        when(userService.getCurrentUser(null)).thenThrow(new UnauthorizedException("Missing X-User-Id header"));

        // Act & Assert
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refresh_withValidToken_returns200() throws Exception {
        // Arrange
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("valid-token")
                .build();
        LoginResponse response = LoginResponse.builder()
                .accessToken("new-access-token")
                .build();

        when(userService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/users/refresh").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"));
    }

    @Test
    void updateProfile_withValidRequest_returns200() throws Exception {
        // Arrange
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .firstName("UpdatedName")
                .build();
        UserResponse response = UserResponse.builder()
                .id(1L)
                .firstName("UpdatedName")
                .build();

        when(userService.updateProfile(eq("1"), any(UpdateProfileRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/api/users/me").header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("UpdatedName"));
    }

    @Test
    void changePassword_withValidRequest_returns204() throws Exception {
        // Arrange
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("OldPassword1!")
                .newPassword("NewPassword1!")
                .build();

        // Act & Assert
        mockMvc.perform(put("/api/users/me/password").header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void forgotPassword_withValidEmail_returns200() throws Exception {
        // Arrange
        ForgotPasswordRequest request = ForgotPasswordRequest.builder()
                .email("test@example.com")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/users/forgot-password").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void resetPassword_withValidRequest_returns200() throws Exception {
        // Arrange
        ResetPasswordRequest request = ResetPasswordRequest.builder()
                .token("valid-token")
                .newPassword("NewPassword1!")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/users/reset-password").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void resetPassword_withInvalidToken_returns401() throws Exception {
        // Arrange
        ResetPasswordRequest request = ResetPasswordRequest.builder()
                .token("garbage")
                .newPassword("NewPassword1!")
                .build();

        org.mockito.Mockito.doThrow(new UnauthorizedException("Invalid or expired reset token"))
                .when(userService)
                .resetPassword(any(ResetPasswordRequest.class));

        // Act & Assert
        mockMvc.perform(post("/api/users/reset-password").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void verifyEmail_withValidToken_returns200() throws Exception {
        // Arrange
        VerifyEmailRequest request = VerifyEmailRequest.builder()
                .token("valid-token")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/users/verify-email").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void resendVerification_withValidEmail_returns200() throws Exception {
        // Arrange
        ResendVerificationRequest request = ResendVerificationRequest.builder()
                .email("test@example.com")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/users/resend-verification").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void deactivateAccount_withValidHeader_returns200() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/users/me/deactivate").header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void reactivateAccount_withValidCredentials_returns200() throws Exception {
        // Arrange
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("Password1!")
                .build();
        LoginResponse response = LoginResponse.builder()
                .accessToken("access-token")
                .build();

        when(userService.reactivateAccount(any(LoginRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/users/reactivate").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"));
    }
}