package com.shopdarcel.common.util;

import java.security.SecureRandom;

/**
 * Small collection of string helpers shared across services, covering
 * gaps the JDK doesn't fill directly. Deliberately does NOT include email
 * validation — use the {@code @Email} bean-validation annotation on
 * request DTOs for that instead, so there's one single source of truth
 * for what counts as a valid email across the whole project.
 */
public final class StringUtil {

    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final SecureRandom RANDOM = new SecureRandom();

    private StringUtil() {
        // Utility class — never instantiated.
    }

    /**
     * True if {@code value} is null, empty, or all whitespace.
     * Unlike {@code String.isBlank()}, this is null-safe — calling
     * {@code value.isBlank()} directly throws NPE if value is null.
     */
    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    /**
     * Truncates {@code value} to at most {@code maxLength} characters, appending "..." if it was cut.
     */
    public static String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength) + "...";
    }

    /**
     * Generates a random alphanumeric string of the given length — useful for
     * things like order reference codes or short-lived verification codes.
     * Uses {@link SecureRandom}, so it is safe for tokens with security relevance.
     */
    public static String randomAlphanumeric(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }

    /**
     * Masks all but the first 2 and last 2 characters of a string, e.g. for logging sensitive values safely.
     */
    public static String mask(String value) {
        if (value == null || value.length() <= 4) {
            return "****";
        }
        return value.substring(0, 2) + "*".repeat(value.length() - 4) + value.substring(value.length() - 2);
    }
}