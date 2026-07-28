package com.shopdarcel.user.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger documentation metadata for user-service.
 * <p>
 * Documents {@code X-User-Id} as a header parameter (rather than a JWT
 * bearer scheme) since api-gateway performs JWT validation and forwards
 * this header — user-service's own endpoints trust it directly rather
 * than validating a token themselves.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI userServiceOpenAPI() {
        Parameter userIdHeader = new Parameter().in("header")
                .name("X-User-Id")
                .description("The authenticated user's ID, normally forwarded by api-gateway after JWT validation. Required on endpoints that act on the current user.")
                .required(false);

        return new OpenAPI().info(new Info().title("ShopDarcel User Service API")
                        .description("Handles user registration, authentication, profile management, and saved addresses for the ShopDarcel e-commerce platform.")
                        .version("1.0.0"))
                .components(new Components().addParameters("X-User-Id", userIdHeader));
    }
}