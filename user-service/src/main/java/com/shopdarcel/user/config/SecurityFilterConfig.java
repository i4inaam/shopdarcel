package com.shopdarcel.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Disables Spring Security's default authentication challenge for
 * user-service's internal HTTP endpoints.
 * <p>
 * Per ARCHITECTURE.md, JWT validation happens exclusively at api-gateway;
 * internal services trust forwarded {@code X-User-Id}/{@code X-User-Role}
 * headers rather than performing their own login-based security checks.
 * user-service specifically also needs certain endpoints (register, login)
 * to be reachable without any prior authentication at all, since a user
 * has no token yet at that point.
 */
@Configuration
@EnableWebSecurity
public class SecurityFilterConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest()
                        .permitAll());

        return http.build();
    }
}