package com.shopdarcel.common.util;

import com.shopdarcel.common.constants.AppConstants;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Small collection of date/time helpers shared across services, so
 * timestamp formatting stays consistent everywhere instead of each
 * service defining its own {@link DateTimeFormatter}.
 */
public final class DateUtil {

    /**
     * Formatter matching {@code AppConstants.TIMESTAMP_FORMAT}, fixed to
     * UTC so formatted output never depends on the JVM's default zone.
     */
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern(AppConstants.TIMESTAMP_FORMAT)
            .withZone(ZoneOffset.UTC);

    private DateUtil() {
        // Utility class — never instantiated.
    }

    /**
     * Current instant, in UTC.
     */
    public static Instant nowUtc() {
        return Instant.now();
    }

    /**
     * Formats an {@link Instant} using the shared {@code AppConstants.TIMESTAMP_FORMAT} pattern.
     */
    public static String format(Instant instant) {
        if (instant == null) {
            return null;
        }
        return TIMESTAMP_FORMATTER.format(instant);
    }

    /**
     * Parses a timestamp string in the shared {@code AppConstants.TIMESTAMP_FORMAT} pattern back into an {@link Instant}.
     */
    public static Instant parse(String timestamp) {
        if (timestamp == null || timestamp.isBlank()) {
            return null;
        }
        return TIMESTAMP_FORMATTER.parse(timestamp, Instant::from);
    }

    /**
     * True if {@code instant} is strictly before now.
     */
    public static boolean isPast(Instant instant) {
        return instant != null && instant.isBefore(Instant.now());
    }

    /**
     * True if {@code instant} is strictly after now.
     */
    public static boolean isFuture(Instant instant) {
        return instant != null && instant.isAfter(Instant.now());
    }
}