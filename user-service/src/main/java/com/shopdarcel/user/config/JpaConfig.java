package com.shopdarcel.user.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Enables JPA auditing (populates {@code createdAt}/{@code updatedAt} on
 * entities extending BaseEntity) as its own configuration class, kept
 * separate from {@code UserServiceApplication} so that web-layer slice
 * tests ({@code @WebMvcTest}) don't attempt to initialize JPA
 * infrastructure they don't need.
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}