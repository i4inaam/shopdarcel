package com.shopdarcel.common.util;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

class DateUtilTest {

    @Test
    void format_thenParse_roundTripsToTheMillisecond() {
        Instant original = Instant.now().truncatedTo(ChronoUnit.MILLIS);

        String formatted = DateUtil.format(original);
        Instant parsed = DateUtil.parse(formatted);

        assertThat(parsed).isEqualTo(original);
    }

    @Test
    void format_returnsNullForNullInput() {
        assertThat(DateUtil.format(null)).isNull();
    }

    @Test
    void parse_returnsNullForNullOrBlankInput() {
        assertThat(DateUtil.parse(null)).isNull();
        assertThat(DateUtil.parse("")).isNull();
        assertThat(DateUtil.parse("   ")).isNull();
    }

    @Test
    void isPast_returnsTrueForInstantBeforeNow() {
        Instant past = Instant.now().minusSeconds(60);

        assertThat(DateUtil.isPast(past)).isTrue();
        assertThat(DateUtil.isFuture(past)).isFalse();
    }

    @Test
    void isFuture_returnsTrueForInstantAfterNow() {
        Instant future = Instant.now().plusSeconds(60);

        assertThat(DateUtil.isFuture(future)).isTrue();
        assertThat(DateUtil.isPast(future)).isFalse();
    }

    @Test
    void isPast_and_isFuture_returnFalseForNull() {
        assertThat(DateUtil.isPast(null)).isFalse();
        assertThat(DateUtil.isFuture(null)).isFalse();
    }
}