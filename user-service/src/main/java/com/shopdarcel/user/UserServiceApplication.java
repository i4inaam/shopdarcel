package com.shopdarcel.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Entry point for the user-service application.
 * <p>
 * Responsible for user registration, login, and JWT issuance/refresh
 * within the ShopDarcel microservices ecosystem. This class triggers
 * Spring Boot's auto-configuration and component scanning for every
 * class under {@code com.shopdarcel.user} and its subpackages
 * (config, controller, service, repository, etc.).
 */
@SpringBootApplication
@EnableCaching
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}