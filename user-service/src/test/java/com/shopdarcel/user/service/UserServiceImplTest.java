package com.shopdarcel.user.service;

import com.shopdarcel.common.exception.*;
import com.shopdarcel.user.constants.AuthMessages;
import com.shopdarcel.user.dto.user.*;
import com.shopdarcel.user.entity.User;
import com.shopdarcel.user.entity.UserRole;
import com.shopdarcel.user.kafka.UserEventProducer;
import com.shopdarcel.user.mapper.UserMapper;
import com.shopdarcel.user.repository.UserRepository;
import com.shopdarcel.user.security.JwtService;
import com.shopdarcel.user.util.UserIdHeaderResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link UserServiceImpl}, covering registration, login,
 * and account lifecycle business logic in isolation from Spring context,
 * the database, and Kafka — all external dependencies are mocked.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserEventProducer eventProducer;

    @Mock
    private LoginAttemptService loginAttemptService;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private UserIdHeaderResolver userIdHeaderResolver;

    @InjectMocks
    private UserServiceImpl userService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .email("test@example.com")
                .password("Password1!")
                .firstName("Inaam")
                .lastName("Haq")
                .termsAccepted(true)
                .build();
    }

    /**
     * Builds a test {@link User} with sensible defaults. Callers can
     * mutate the returned instance further for scenario-specific state
     * (e.g. {@code accountLockedAt}).
     */
    private User createUser(boolean active) {
        User user = User.builder()
                .email("test@example.com")
                .password("hashed-password")
                .firstName("Inaam")
                .lastName("Haq")
                .role(UserRole.ROLE_USER)
                .build();
        user.setId(1L);
        user.setActive(active);
        return user;
    }

    private LoginRequest createLoginRequest() {
        return LoginRequest.builder()
                .email("test@example.com")
                .password("Password1!")
                .build();
    }

    // ---------- register() ----------

    @Test
    void register_withNewEmail_savesUserAndReturnsResponse() {
        // Arrange
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password1!")).thenReturn("hashed-password");

        User savedUser = createUser(true);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse expectedResponse = UserResponse.builder()
                .id(1L)
                .build();
        when(userMapper.toResponse(savedUser)).thenReturn(expectedResponse);

        // Act
        UserResponse result = userService.register(registerRequest);

        // Assert
        assertThat(result).isEqualTo(expectedResponse);
        verify(userRepository, times(2)).save(any(User.class));
        verify(eventProducer).publishUserRegistered(any());
        verify(eventProducer).publishEmailVerificationRequested(any());
    }

    @Test
    void register_withExistingEmail_throwsConflictException() {
        // Arrange
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.register(registerRequest)).isInstanceOf(ConflictException.class)
                .hasMessage("Email is already registered");
    }

    @Test
    void register_withFutureBirthYear_throwsValidationException() {
        // Arrange
        registerRequest.setBirthYear(2090);

        // Act & Assert
        assertThatThrownBy(() -> userService.register(registerRequest)).isInstanceOf(ValidationException.class);
    }

    // ---------- login() ----------

    @Test
    void login_withCorrectCredentials_returnsTokensAndUpdatesLastLogin() {
        // Arrange
        User user = createUser(true);
        LoginRequest loginRequest = createLoginRequest();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password1!", "hashed-password")).thenReturn(true);
        when(jwtService.generateAccessToken(user)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh-token");
        when(userMapper.toResponse(user)).thenReturn(UserResponse.builder()
                .id(1L)
                .build());

        // Act
        LoginResponse result = userService.login(loginRequest);

        // Assert
        assertThat(result.getAccessToken()).isEqualTo("access-token");
        assertThat(result.getRefreshToken()).isEqualTo("refresh-token");
        verify(userRepository).save(user);
        assertThat(user.getFailedLoginAttempts()).isEqualTo(0);
        assertThat(user.getLastLoginAt()).isNotNull();
    }

    @Test
    void login_withWrongPassword_throwsUnauthorizedAndRecordsFailedAttempt() {
        // Arrange
        User user = createUser(true);
        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("WrongPassword1!")
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPassword1!", "hashed-password")).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> userService.login(loginRequest)).isInstanceOf(UnauthorizedException.class)
                .hasMessage(AuthMessages.INVALID_CREDENTIALS);

        verify(loginAttemptService).recordFailedAttempt(user);
    }

    @Test
    void login_withNonExistentEmail_throwsUnauthorized() {
        // Arrange
        LoginRequest loginRequest = createLoginRequest();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.login(loginRequest)).isInstanceOf(UnauthorizedException.class)
                .hasMessage(AuthMessages.INVALID_CREDENTIALS);
    }

    @Test
    void login_withLockedAccount_throwsForbidden() {
        // Arrange
        User user = createUser(true);
        user.setAccountLockedAt(Instant.now());
        LoginRequest loginRequest = createLoginRequest();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Act & Assert
        assertThatThrownBy(() -> userService.login(loginRequest)).isInstanceOf(ForbiddenException.class)
                .hasMessage(AuthMessages.ACCOUNT_LOCKED);
    }

    @Test
    void login_withDeactivatedAccount_throwsForbidden() {
        // Arrange
        User user = createUser(false);
        LoginRequest loginRequest = createLoginRequest();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Act & Assert
        assertThatThrownBy(() -> userService.login(loginRequest)).isInstanceOf(ForbiddenException.class)
                .hasMessage(AuthMessages.ACCOUNT_DEACTIVATED);
    }

    // ---------- refreshToken() ----------

    @Test
    void refreshToken_withValidToken_returnsNewTokenPair() {
        // Arrange
        User user = createUser(true);
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("valid-refresh-token")
                .build();

        when(jwtService.isTokenValid("valid-refresh-token")).thenReturn(true);
        when(jwtService.extractUserId("valid-refresh-token")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn("new-access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn("new-refresh-token");
        when(userMapper.toResponse(user)).thenReturn(UserResponse.builder()
                .id(1L)
                .build());

        // Act
        LoginResponse result = userService.refreshToken(request);

        // Assert
        assertThat(result.getAccessToken()).isEqualTo("new-access-token");
        assertThat(result.getRefreshToken()).isEqualTo("new-refresh-token");
    }

    @Test
    void refreshToken_withInvalidToken_throwsUnauthorized() {
        // Arrange
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("garbage")
                .build();
        when(jwtService.isTokenValid("garbage")).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> userService.refreshToken(request)).isInstanceOf(UnauthorizedException.class)
                .hasMessage(AuthMessages.INVALID_REFRESH_TOKEN);
    }

    @Test
    void refreshToken_forDeactivatedAccount_throwsForbidden() {
        // Arrange
        User user = createUser(false);
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("valid-refresh-token")
                .build();

        when(jwtService.isTokenValid("valid-refresh-token")).thenReturn(true);
        when(jwtService.extractUserId("valid-refresh-token")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThatThrownBy(() -> userService.refreshToken(request)).isInstanceOf(ForbiddenException.class)
                .hasMessage(AuthMessages.ACCOUNT_DEACTIVATED);
    }

    @Test
    void refreshToken_forLockedAccount_throwsForbidden() {
        // Arrange
        User user = createUser(true);
        user.setAccountLockedAt(Instant.now());
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("valid-refresh-token")
                .build();

        when(jwtService.isTokenValid("valid-refresh-token")).thenReturn(true);
        when(jwtService.extractUserId("valid-refresh-token")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThatThrownBy(() -> userService.refreshToken(request)).isInstanceOf(ForbiddenException.class)
                .hasMessage(AuthMessages.ACCOUNT_LOCKED);
    }

    // ---------- changePassword() ----------

    @Test
    void changePassword_withCorrectCurrentPassword_updatesPassword() {
        // Arrange
        User user = createUser(true);
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("OldPassword1!")
                .newPassword("NewPassword1!")
                .build();

        when(userIdHeaderResolver.resolve("1")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("OldPassword1!", "hashed-password")).thenReturn(true);
        when(passwordEncoder.matches("NewPassword1!", "hashed-password")).thenReturn(false);
        when(passwordEncoder.encode("NewPassword1!")).thenReturn("new-hash");

        // Act
        userService.changePassword("1", request);

        // Assert
        assertThat(user.getPassword()).isEqualTo("new-hash");
        verify(eventProducer).publishPasswordChanged(any());
    }

    @Test
    void changePassword_withWrongCurrentPassword_throwsUnauthorized() {
        // Arrange
        User user = createUser(true);
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("WrongPassword1!")
                .newPassword("NewPassword1!")
                .build();

        when(userIdHeaderResolver.resolve("1")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPassword1!", "hashed-password")).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> userService.changePassword("1", request)).isInstanceOf(UnauthorizedException.class)
                .hasMessage(AuthMessages.CURRENT_PASSWORD_INCORRECT);
    }

    @Test
    void changePassword_withSameNewPassword_throwsConflict() {
        // Arrange
        User user = createUser(true);
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("Password1!")
                .newPassword("Password1!")
                .build();

        when(userIdHeaderResolver.resolve("1")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password1!", "hashed-password")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.changePassword("1", request)).isInstanceOf(ConflictException.class)
                .hasMessage(AuthMessages.NEW_PASSWORD_SAME_AS_OLD);
    }

    // ---------- deactivateAccount() / reactivateAccount() ----------

    @Test
    void deactivateAccount_setsActiveFalseAndPublishesEvent() {
        // Arrange
        User user = createUser(true);

        when(userIdHeaderResolver.resolve("1")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        userService.deactivateAccount("1");

        // Assert
        assertThat(user.isActive()).isFalse();
        verify(eventProducer).publishAccountDeactivated(any());
    }

    @Test
    void reactivateAccount_withCorrectCredentials_setsActiveTrueAndReturnsTokens() {
        // Arrange
        User user = createUser(false);
        LoginRequest request = createLoginRequest();

        Cache mockCache = mock(Cache.class);
        when(cacheManager.getCache("userProfiles")).thenReturn(mockCache);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password1!", "hashed-password")).thenReturn(true);
        when(jwtService.generateAccessToken(user)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh-token");
        when(userMapper.toResponse(user)).thenReturn(UserResponse.builder()
                .id(1L)
                .build());

        // Act
        LoginResponse result = userService.reactivateAccount(request);

        // Assert
        assertThat(user.isActive()).isTrue();
        assertThat(result.getAccessToken()).isEqualTo("access-token");
    }

    @Test
    void reactivateAccount_withAlreadyActiveAccount_throwsConflict() {
        // Arrange
        User user = createUser(true);
        LoginRequest request = createLoginRequest();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password1!", "hashed-password")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.reactivateAccount(request)).isInstanceOf(ConflictException.class)
                .hasMessage(AuthMessages.ACCOUNT_ALREADY_ACTIVE);
    }

    @Test
    void reactivateAccount_withWrongPassword_throwsUnauthorized() {
        // Arrange
        User user = createUser(false);
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("WrongPassword1!")
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPassword1!", "hashed-password")).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> userService.reactivateAccount(request)).isInstanceOf(UnauthorizedException.class)
                .hasMessage(AuthMessages.INVALID_CREDENTIALS);
    }

    // ---------- getCurrentUser() ----------

    @Test
    void getCurrentUser_withValidId_returnsProfile() {
        // Arrange
        User user = createUser(true);
        when(userIdHeaderResolver.resolve("1")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(UserResponse.builder()
                .id(1L)
                .build());

        // Act
        UserResponse result = userService.getCurrentUser("1");

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getCurrentUser_withNonExistentId_throwsResourceNotFound() {
        // Arrange
        when(userIdHeaderResolver.resolve("999")).thenReturn(999L);
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.getCurrentUser("999")).isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");
    }

// ---------- updateProfile() ----------

    @Test
    void updateProfile_withPartialFields_updatesOnlyProvidedFields() {
        // Arrange
        User user = createUser(true);
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .firstName("UpdatedName")
                .build();

        when(userIdHeaderResolver.resolve("1")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(UserResponse.builder()
                .id(1L)
                .build());

        // Act
        userService.updateProfile("1", request);

        // Assert
        assertThat(user.getFirstName()).isEqualTo("UpdatedName");
        assertThat(user.getLastName()).isEqualTo("Haq"); // unchanged
    }

    @Test
    void updateProfile_withInvalidBirthYear_throwsValidationException() {
        // Arrange
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .birthYear(1500)
                .build();

        when(userIdHeaderResolver.resolve("1")).thenReturn(1L);

        // Act & Assert
        assertThatThrownBy(() -> userService.updateProfile("1", request)).isInstanceOf(ValidationException.class);
    }

    @Test
    void updateProfile_withOnlyLastName_updatesOnlyLastName() {
        // Arrange
        User user = createUser(true);
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .lastName("UpdatedLastName")
                .build();

        when(userIdHeaderResolver.resolve("1")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(UserResponse.builder()
                .id(1L)
                .build());

        // Act
        userService.updateProfile("1", request);

        // Assert
        assertThat(user.getLastName()).isEqualTo("UpdatedLastName");
        assertThat(user.getFirstName()).isEqualTo("Inaam"); // unchanged
    }

    @Test
    void updateProfile_withOnlyBirthYear_updatesOnlyBirthYear() {
        // Arrange
        User user = createUser(true);
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .birthYear(1995)
                .build();

        when(userIdHeaderResolver.resolve("1")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(UserResponse.builder()
                .id(1L)
                .build());

        // Act
        userService.updateProfile("1", request);

        // Assert
        assertThat(user.getBirthYear()).isEqualTo(1995);
        assertThat(user.getFirstName()).isEqualTo("Inaam"); // unchanged
    }

    @Test
    void updateProfile_withOnlyGender_updatesOnlyGender() {
        // Arrange
        User user = createUser(true);
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .gender(com.shopdarcel.user.entity.Gender.MALE)
                .build();

        when(userIdHeaderResolver.resolve("1")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(UserResponse.builder()
                .id(1L)
                .build());

        // Act
        userService.updateProfile("1", request);

        // Assert
        assertThat(user.getGender()).isEqualTo(com.shopdarcel.user.entity.Gender.MALE);
        assertThat(user.getFirstName()).isEqualTo("Inaam"); // unchanged
    }

// ---------- forgotPassword() ----------

    @Test
    void forgotPassword_withExistingEmail_generatesTokenAndPublishesEvent() {
        // Arrange
        User user = createUser(true);
        ForgotPasswordRequest request = ForgotPasswordRequest.builder()
                .email("test@example.com")
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Act
        userService.forgotPassword(request);

        // Assert
        assertThat(user.getResetTokenHash()).isNotNull();
        assertThat(user.getResetTokenExpiresAt()).isNotNull();
        verify(eventProducer).publishPasswordResetRequested(any());
    }

    @Test
    void forgotPassword_withNonExistentEmail_doesNothing() {
        // Arrange
        ForgotPasswordRequest request = ForgotPasswordRequest.builder()
                .email("nobody@example.com")
                .build();
        when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        // Act
        userService.forgotPassword(request);

        // Assert
        verify(eventProducer, never()).publishPasswordResetRequested(any());
        verify(userRepository, never()).save(any());
    }

// ---------- resetPassword() ----------

    @Test
    void resetPassword_withValidToken_updatesPasswordAndClearsToken() {
        // Arrange
        User user = createUser(true);
        user.setResetTokenHash("some-hash");
        user.setResetTokenExpiresAt(Instant.now()
                .plusSeconds(600));

        ResetPasswordRequest request = ResetPasswordRequest.builder()
                .token("raw-token")
                .newPassword("NewPassword1!")
                .build();

        Cache mockCache = mock(Cache.class);
        when(cacheManager.getCache("userProfiles")).thenReturn(mockCache);
        when(userRepository.findByResetTokenHash(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("NewPassword1!")).thenReturn("new-hash");

        // Act
        userService.resetPassword(request);

        // Assert
        assertThat(user.getPassword()).isEqualTo("new-hash");
        assertThat(user.getResetTokenHash()).isNull();
        assertThat(user.getResetTokenExpiresAt()).isNull();
        verify(eventProducer).publishPasswordChanged(any());
    }

    @Test
    void resetPassword_withExpiredToken_throwsUnauthorized() {
        // Arrange
        User user = createUser(true);
        user.setResetTokenHash("some-hash");
        user.setResetTokenExpiresAt(Instant.now()
                .minusSeconds(600));

        ResetPasswordRequest request = ResetPasswordRequest.builder()
                .token("raw-token")
                .newPassword("NewPassword1!")
                .build();

        when(userRepository.findByResetTokenHash(any())).thenReturn(Optional.of(user));

        // Act & Assert
        assertThatThrownBy(() -> userService.resetPassword(request)).isInstanceOf(UnauthorizedException.class)
                .hasMessage(AuthMessages.INVALID_EXPIRED_RESET_TOKEN);
    }

    @Test
    void resetPassword_withUnknownToken_throwsUnauthorized() {
        // Arrange
        ResetPasswordRequest request = ResetPasswordRequest.builder()
                .token("garbage")
                .newPassword("NewPassword1!")
                .build();

        when(userRepository.findByResetTokenHash(any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.resetPassword(request)).isInstanceOf(UnauthorizedException.class)
                .hasMessage(AuthMessages.INVALID_EXPIRED_RESET_TOKEN);
    }

    @Test
    void resetPassword_withNullExpiresAt_throwsUnauthorized() {
        // Arrange
        User user = createUser(true);
        user.setResetTokenHash("some-hash");
        user.setResetTokenExpiresAt(null);

        ResetPasswordRequest request = ResetPasswordRequest.builder()
                .token("raw-token")
                .newPassword("NewPassword1!")
                .build();

        when(userRepository.findByResetTokenHash(any())).thenReturn(Optional.of(user));

        // Act & Assert
        assertThatThrownBy(() -> userService.resetPassword(request)).isInstanceOf(UnauthorizedException.class)
                .hasMessage(AuthMessages.INVALID_EXPIRED_RESET_TOKEN);
    }

// ---------- verifyEmail() ----------

    @Test
    void verifyEmail_withValidToken_marksVerified() {
        // Arrange
        User user = createUser(true);
        user.setEmailTokenHash("some-hash");
        user.setEmailTokenExpiresAt(Instant.now()
                .plusSeconds(600));

        VerifyEmailRequest request = VerifyEmailRequest.builder()
                .token("raw-token")
                .build();
        when(userRepository.findByEmailTokenHash(any())).thenReturn(Optional.of(user));
        Cache mockCache = mock(Cache.class);
        when(cacheManager.getCache("userProfiles")).thenReturn(mockCache);

        // Act
        userService.verifyEmail(request);

        // Assert
        assertThat(user.isEmailVerified()).isTrue();
        assertThat(user.getEmailTokenHash()).isNull();
    }

    @Test
    void verifyEmail_withNullExpiresAt_throwsUnauthorized() {
        // Arrange
        User user = createUser(true);
        user.setEmailTokenHash("some-hash");
        user.setEmailTokenExpiresAt(null);

        VerifyEmailRequest request = VerifyEmailRequest.builder()
                .token("raw-token")
                .build();
        when(userRepository.findByEmailTokenHash(any())).thenReturn(Optional.of(user));

        // Act & Assert
        assertThatThrownBy(() -> userService.verifyEmail(request)).isInstanceOf(UnauthorizedException.class)
                .hasMessage(AuthMessages.INVALID_EXPIRED_VERIFICATION_TOKEN);
    }

    @Test
    void verifyEmail_withExpiredToken_throwsUnauthorized() {
        // Arrange
        User user = createUser(true);
        user.setEmailTokenHash("some-hash");
        user.setEmailTokenExpiresAt(Instant.now()
                .minusSeconds(600));

        VerifyEmailRequest request = VerifyEmailRequest.builder()
                .token("raw-token")
                .build();
        when(userRepository.findByEmailTokenHash(any())).thenReturn(Optional.of(user));

        // Act & Assert
        assertThatThrownBy(() -> userService.verifyEmail(request)).isInstanceOf(UnauthorizedException.class)
                .hasMessage(AuthMessages.INVALID_EXPIRED_VERIFICATION_TOKEN);
    }

// ---------- resendVerification() ----------

    @Test
    void resendVerification_forUnverifiedUser_issuesNewToken() {
        // Arrange
        User user = createUser(true);
        user.setEmailVerified(false);
        ResendVerificationRequest request = ResendVerificationRequest.builder()
                .email("test@example.com")
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Act
        userService.resendVerification(request);

        // Assert
        assertThat(user.getEmailTokenHash()).isNotNull();
        verify(eventProducer).publishEmailVerificationRequested(any());
    }

    @Test
    void resendVerification_forAlreadyVerifiedUser_doesNothing() {
        // Arrange
        User user = createUser(true);
        user.setEmailVerified(true);
        ResendVerificationRequest request = ResendVerificationRequest.builder()
                .email("test@example.com")
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Act
        userService.resendVerification(request);

        // Assert
        verify(eventProducer, never()).publishEmailVerificationRequested(any());
    }
}