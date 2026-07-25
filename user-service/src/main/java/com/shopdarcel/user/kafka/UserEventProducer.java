package com.shopdarcel.user.kafka;

import com.shopdarcel.common.constants.KafkaTopics;
import com.shopdarcel.common.dto.kafka.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Publishes user-related domain events to Kafka.
 * <p>
 * Per ARCHITECTURE.md, this is the only way user-service communicates
 * with other services (e.g. notification-service) — zero direct REST
 * calls between services.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Publishes a {@link UserRegisteredEvent} after successful registration.
     *
     * @param event the fully populated event to publish
     */
    public void publishUserRegistered(UserRegisteredEvent event) {
        try {
            kafkaTemplate.send(KafkaTopics.USER_REGISTERED, event.getUserId()
                            .toString(), event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish user.registered event for userId={}", event.getUserId(), ex);
                        } else {
                            log.info("Published user.registered event for userId={}", event.getUserId());
                        }
                    });
        } catch (Exception ex) {
            log.error("Unexpected error publishing user.registered event for userId={}", event.getUserId(), ex);
        }
    }

    public void publishPasswordChanged(PasswordChangedEvent event) {
        try {
            kafkaTemplate.send(KafkaTopics.USER_PASSWORD_CHANGED, event.getUserId()
                            .toString(), event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish user.password.changed event for userId={}", event.getUserId(), ex);
                        } else {
                            log.info("Published user.password.changed event for userId={}", event.getUserId());
                        }
                    });
        } catch (Exception ex) {
            log.error("Unexpected error publishing user.password.changed event for userId={}", event.getUserId(), ex);
        }
    }

    public void publishAccountLocked(AccountLockedEvent event) {
        try {
            kafkaTemplate.send(KafkaTopics.USER_ACCOUNT_LOCKED, event.getUserId()
                            .toString(), event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish user.account.locked event for userId={}", event.getUserId(), ex);
                        } else {
                            log.info("Published user.account.locked event for userId={}", event.getUserId());
                        }
                    });
        } catch (Exception ex) {
            log.error("Unexpected error publishing user.account.locked event for userId={}", event.getUserId(), ex);
        }
    }

    public void publishPasswordResetRequested(PasswordResetRequestedEvent event) {
        try {
            kafkaTemplate.send(KafkaTopics.USER_PASSWORD_RESET_REQUESTED, event.getUserId()
                            .toString(), event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish user.password.reset.requested event for userId={}", event.getUserId(), ex);
                        } else {
                            log.info("Published user.password.reset.requested event for userId={}", event.getUserId());
                        }
                    });
        } catch (Exception ex) {
            log.error("Unexpected error publishing user.password.reset.requested event for userId={}", event.getUserId(), ex);
        }
    }

    public void publishEmailVerificationRequested(EmailVerificationRequestedEvent event) {
        try {
            kafkaTemplate.send(KafkaTopics.USER_EMAIL_VERIFICATION_REQUESTED, event.getUserId()
                            .toString(), event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish user.email.verification.requested event for userId={}", event.getUserId(), ex);
                        } else {
                            log.info("Published user.email.verification.requested event for userId={}", event.getUserId());
                        }
                    });
        } catch (Exception ex) {
            log.error("Unexpected error publishing user.email.verification.requested event for userId={}", event.getUserId(), ex);
        }
    }

    public void publishAccountDeactivated(AccountDeactivatedEvent event) {
        try {
            kafkaTemplate.send(KafkaTopics.USER_ACCOUNT_DEACTIVATED, event.getUserId()
                            .toString(), event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish user.account.deactivated event for userId={}", event.getUserId(), ex);
                        } else {
                            log.info("Published user.account.deactivated event for userId={}", event.getUserId());
                        }
                    });
        } catch (Exception ex) {
            log.error("Unexpected error publishing user.account.deactivated event for userId={}", event.getUserId(), ex);
        }
    }
}