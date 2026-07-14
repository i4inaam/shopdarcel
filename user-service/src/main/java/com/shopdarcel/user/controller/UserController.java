package com.shopdarcel.user.controller;

import com.shopdarcel.user.dto.LoginRequest;
import com.shopdarcel.user.dto.LoginResponse;
import com.shopdarcel.user.dto.RegisterRequest;
import com.shopdarcel.user.dto.UserResponse;
import com.shopdarcel.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}