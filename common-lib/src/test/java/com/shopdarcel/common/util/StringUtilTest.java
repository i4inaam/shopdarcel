package com.shopdarcel.common.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StringUtilTest {

    @Test
    void isBlank_returnsTrueForNullEmptyAndWhitespace() {
        assertThat(StringUtil.isBlank(null)).isTrue();
        assertThat(StringUtil.isBlank("")).isTrue();
        assertThat(StringUtil.isBlank("   ")).isTrue();
    }

    @Test
    void isBlank_returnsFalseForNonBlankValue() {
        assertThat(StringUtil.isBlank("hello")).isFalse();
    }

    @Test
    void truncate_cutsAndAppendsEllipsisWhenTooLong() {
        assertThat(StringUtil.truncate("hello world", 5)).isEqualTo("hello...");
    }

    @Test
    void truncate_leavesShortStringsUnchanged() {
        assertThat(StringUtil.truncate("hi", 5)).isEqualTo("hi");
    }

    @Test
    void truncate_returnsNullForNullInput() {
        assertThat(StringUtil.truncate(null, 5)).isNull();
    }

    @Test
    void randomAlphanumeric_producesRequestedLength() {
        String result = StringUtil.randomAlphanumeric(12);

        assertThat(result).hasSize(12);
        assertThat(result).matches("[A-Za-z0-9]+");
    }

    @Test
    void randomAlphanumeric_producesDifferentValuesOnEachCall() {
        String first = StringUtil.randomAlphanumeric(20);
        String second = StringUtil.randomAlphanumeric(20);

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void mask_hidesMiddleCharactersKeepingFirstAndLastTwo() {
        assertThat(StringUtil.mask("secretvalue")).isEqualTo("se*******ue");
    }

    @Test
    void mask_returnsAllAsterisksForShortStrings() {
        assertThat(StringUtil.mask("abc")).isEqualTo("****");
        assertThat(StringUtil.mask(null)).isEqualTo("****");
    }
}