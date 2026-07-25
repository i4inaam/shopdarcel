package com.shopdarcel.user.util;

import com.shopdarcel.common.exception.UnauthorizedException;
import com.shopdarcel.user.constants.AuthMessages;
import org.springframework.stereotype.Component;

/**
 * Parses and validates the raw {@code X-User-Id} header value, shared
 * across services that need to identify the authenticated user from a
 * gateway-forwarded header (rather than duplicating this logic in each).
 */
@Component
public class UserIdHeaderResolver {

    public Long resolve(String userIdHeader) {
        if (userIdHeader == null || userIdHeader.isBlank()) {
            throw new UnauthorizedException(AuthMessages.MISSING_USER_ID_HEADER);
        }
        try {
            return Long.parseLong(userIdHeader);
        } catch (NumberFormatException ex) {
            throw new UnauthorizedException(AuthMessages.MISSING_USER_ID_HEADER);
        }
    }
}