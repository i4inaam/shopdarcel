package com.shopdarcel.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Security-related bean definitions for user-service.
 * <p>
 * Only exposes a {@link PasswordEncoder} bean for now — user-service does
 * not validate incoming JWTs (that's api-gateway's responsibility per
 * ARCHITECTURE.md); it only needs to hash passwords at registration and
 * verify them at login.
 */
@Configuration
public class SecurityConfig {

    /**
     * Provides BCrypt password hashing, per ARCHITECTURE.md's security
     * architecture. BCrypt automatically incorporates a random salt per
     * password and is deliberately slow (computationally expensive) to
     * resist brute-force attacks — unlike fast hashes such as SHA-256,
     * which are unsuitable for password storage.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}