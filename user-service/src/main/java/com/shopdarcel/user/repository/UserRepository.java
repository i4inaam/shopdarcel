package com.shopdarcel.user.repository;

import com.shopdarcel.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link User} persistence.
 * <p>
 * Extends {@link JpaRepository} to inherit standard CRUD operations
 * ({@code save}, {@code findById}, {@code deleteById}, etc.) without any
 * manual implementation — Spring Data generates the implementation at
 * runtime based on this interface.
 * <p>
 * Custom finder methods below follow Spring Data's derived query method
 * naming convention: the method name itself is parsed to build the query,
 * so no JPQL or SQL needs to be written for these simple lookups.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     * <p>
     * Returns {@link Optional} rather than a raw {@code User}, forcing
     * callers to explicitly handle the "not found" case rather than
     * risking a {@code NullPointerException}.
     *
     * @param email the email address to search for
     * @return an {@link Optional} containing the matching user, or empty if none exists
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks whether a user with the given email already exists.
     * <p>
     * More efficient than {@link #findByEmail(String)} when only existence
     * needs to be checked (e.g. during registration validation) — Spring
     * Data generates this as a lightweight {@code EXISTS} query rather than
     * fetching the full entity.
     *
     * @param email the email address to check
     * @return {@code true} if a user with this email exists, {@code false} otherwise
     */
    boolean existsByEmail(String email);
}