package com.shopdarcel.user.service;

import com.shopdarcel.common.dto.kafka.UserRegisteredEvent;
import com.shopdarcel.common.exception.ConflictException;
import com.shopdarcel.common.exception.ForbiddenException;
import com.shopdarcel.common.exception.UnauthorizedException;
import com.shopdarcel.user.config.CorrelationIdFilter;
import com.shopdarcel.user.constants.AuthMessages;
import com.shopdarcel.user.dto.*;
import com.shopdarcel.user.entity.User;
import com.shopdarcel.user.entity.UserRole;
import com.shopdarcel.user.kafka.UserEventProducer;
import com.shopdarcel.user.mapper.UserMapper;
import com.shopdarcel.user.repository.UserRepository;
import com.shopdarcel.user.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Default implementation of {@link UserService}.
 * <p>
 * Handles password hashing, server-side audit timestamps, and default
 * role assignment — logic that deliberately does not live in
 * {@link UserMapper}, since it's business logic rather than mechanical
 * field mapping.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserEventProducer eventProducer;
    private final JwtService jwtService;
    private final LoginAttemptService loginAttemptService;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException(AuthMessages.EMAIL_ALREADY_REGISTERED);
        }

        Instant now = Instant.now();

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .termsAcceptedAt(now)
                .passwordChangedAt(now)
                .role(UserRole.ROLE_USER)
                .build();

        User savedUser = userRepository.save(user);

        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .eventId(java.util.UUID.randomUUID())
                .occurredAt(now)
                .correlationId(MDC.get(CorrelationIdFilter.MDC_KEY))
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .fullName(buildFullName(savedUser))
                .role(savedUser.getRole().name())
                .build();

        eventProducer.publishUserRegistered(event);

        return userMapper.toResponse(savedUser);
    }

    private String buildFullName(User user) {
        if (user.getLastName() == null || user.getLastName().isBlank()) {
            return user.getFirstName();
        }
        return user.getFirstName() + " " + user.getLastName();
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException(AuthMessages.INVALID_CREDENTIALS));

        if (user.getAccountLockedAt() != null) {
            throw new ForbiddenException(AuthMessages.ACCOUNT_LOCKED);
        }

        if (!user.isActive()) {
            throw new ForbiddenException(AuthMessages.ACCOUNT_DEACTIVATED);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            loginAttemptService.recordFailedAttempt(user);
            throw new UnauthorizedException(AuthMessages.INVALID_CREDENTIALS);
        }

        user.setFailedLoginAttempts(0);
        user.setAccountLockedAt(null);
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userMapper.toResponse(user))
                .build();
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        String token = request.getRefreshToken();

        if (!jwtService.isTokenValid(token)) {
            throw new UnauthorizedException(AuthMessages.INVALID_REFRESH_TOKEN);
        }

        Long userId = jwtService.extractUserId(token);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException(AuthMessages.INVALID_REFRESH_TOKEN));

        if (!user.isActive()) {
            throw new ForbiddenException(AuthMessages.ACCOUNT_DEACTIVATED);
        }

        if (user.getAccountLockedAt() != null) {
            throw new ForbiddenException(AuthMessages.ACCOUNT_LOCKED);
        }

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .user(userMapper.toResponse(user))
                .build();
    }
}