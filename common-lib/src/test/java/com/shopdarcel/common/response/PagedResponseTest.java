package com.shopdarcel.common.response;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PagedResponseTest {

    @Test
    void of_mapsSpringDataPageMetadataCorrectly() {
        var page = new PageImpl<>(List.of("a", "b"), PageRequest.of(0, 2), 5);

        PagedResponse<String> response = PagedResponse.of(page);

        assertThat(response.getContent()).containsExactly("a", "b");
        assertThat(response.getPageNumber()).isZero();
        assertThat(response.getPageSize()).isEqualTo(2);
        assertThat(response.getTotalElements()).isEqualTo(5);
        assertThat(response.getTotalPages()).isEqualTo(3);
        assertThat(response.isFirst()).isTrue();
        assertThat(response.isLast()).isFalse();
    }

    @Test
    void of_onLastPage_marksLastTrueAndFirstFalse() {
        var page = new PageImpl<>(List.of("e"), PageRequest.of(2, 2), 5);

        PagedResponse<String> response = PagedResponse.of(page);

        assertThat(response.isLast()).isTrue();
        assertThat(response.isFirst()).isFalse();
    }

    @Test
    void of_withMapper_transformsEachElement() {
        var page = new PageImpl<>(List.of(1, 2, 3), PageRequest.of(0, 3), 3);

        PagedResponse<String> response = PagedResponse.of(page, i -> "item-" + i);

        assertThat(response.getContent()).containsExactly("item-1", "item-2", "item-3");
    }
}