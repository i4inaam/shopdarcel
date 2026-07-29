package com.shopdarcel.user.service;

import com.shopdarcel.user.entity.User;
import com.shopdarcel.user.entity.UserRole;
import com.shopdarcel.user.kafka.UserEventProducer;
import com.shopdarcel.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link LoginAttemptService}, covering the brute-force
 * lockout threshold and its associated Kafka event.
 */
@ExtendWith(MockitoExtension.class)
class LoginAttemptServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserEventProducer eventProducer;

    @InjectMocks
    private LoginAttemptService loginAttemptService;

    private User createUser(int failedAttempts) {
        User user = User.builder()
                .email("test@example.com")
                .role(UserRole.ROLE_USER)
                .build();
        user.setId(1L);
        user.setFailedLoginAttempts(failedAttempts);
        return user;
    }

    @Test
    void recordFailedAttempt_belowThreshold_incrementsCountAndDoesNotLock() {
        // Arrange
        User user = createUser(2);

        // Act
        loginAttemptService.recordFailedAttempt(user);

        // Assert
        assertThat(user.getFailedLoginAttempts()).isEqualTo(3);
        assertThat(user.getAccountLockedAt()).isNull();
        verify(eventProducer, never()).publishAccountLocked(any());
    }

    @Test
    void recordFailedAttempt_atThreshold_locksAccountAndPublishesEvent() {
        // Arrange
        User user = createUser(4);

        // Act
        loginAttemptService.recordFailedAttempt(user);

        // Assert
        assertThat(user.getFailedLoginAttempts()).isEqualTo(5);
        assertThat(user.getAccountLockedAt()).isNotNull();
        verify(eventProducer).publishAccountLocked(any());
        verify(userRepository).save(user);
    }
}