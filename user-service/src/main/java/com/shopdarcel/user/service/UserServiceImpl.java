package com.shopdarcel.user.service;

import com.shopdarcel.common.exception.ConflictException;
import com.shopdarcel.user.dto.RegisterRequest;
import com.shopdarcel.user.dto.UserResponse;
import com.shopdarcel.user.entity.User;
import com.shopdarcel.user.entity.UserRole;
import com.shopdarcel.user.mapper.UserMapper;
import com.shopdarcel.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email is already registered");
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

        return userMapper.toResponse(savedUser);
    }
}