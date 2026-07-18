package com.shopdarcel.user.service;

import com.shopdarcel.common.dto.kafka.AccountLockedEvent;
import com.shopdarcel.user.config.CorrelationIdFilter;
import com.shopdarcel.user.entity.User;
import com.shopdarcel.user.kafka.UserEventProducer;
import com.shopdarcel.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Records failed login attempts in their own independent transaction.
 * <p>
 * Separated into its own bean (rather than a private method on
 * {@link UserServiceImpl}) because {@code @Transactional(propagation = REQUIRES_NEW)}
 * only takes effect on calls made through Spring's proxy — a call from
 * within the same class bypasses the proxy entirely and would be silently
 * ignored, undoing the attempt count whenever the caller's own transaction
 * later rolls back.
 */
@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private final UserRepository userRepository;
    private final UserEventProducer eventProducer;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFailedAttempt(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);

        if (attempts >= MAX_FAILED_ATTEMPTS) {
            Instant lockedAt = Instant.now();
            user.setAccountLockedAt(lockedAt);
            userRepository.save(user);

            AccountLockedEvent event = AccountLockedEvent.builder()
                    .eventId(java.util.UUID.randomUUID())
                    .occurredAt(lockedAt)
                    .correlationId(MDC.get(CorrelationIdFilter.MDC_KEY))
                    .userId(user.getId())
                    .email(user.getEmail())
                    .build();

            eventProducer.publishAccountLocked(event);
        } else {
            userRepository.save(user);
        }
    }
}