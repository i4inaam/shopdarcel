package com.shopdarcel.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Standard wrapper for any paginated list endpoint across ShopDarcel.
 * Built directly from a Spring Data {@link Page}, so a service layer
 * that already returns {@code Page<T>} can construct this in one line.
 *
 * @param <T> type of each element in {@code content}
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {

    /**
     * The page's actual content.
     */
    private List<T> content;

    /**
     * Current page number (0-based).
     */
    private int pageNumber;

    /**
     * Number of elements requested per page.
     */
    private int pageSize;

    /**
     * Total number of elements across all pages.
     */
    private long totalElements;

    /**
     * Total number of pages.
     */
    private int totalPages;

    /**
     * True if this is the last page.
     */
    private boolean last;

    /**
     * True if this is the first page.
     */
    private boolean first;

    /**
     * Builds a {@link PagedResponse} directly from a Spring Data {@link Page},
     * optionally re-mapping each element via {@code mapper} (e.g. entity -> DTO).
     */
    public static <T, R> PagedResponse<R> of(Page<T> page, java.util.function.Function<T, R> mapper) {
        return PagedResponse.<R>builder()
                .content(page.getContent()
                        .stream()
                        .map(mapper)
                        .toList())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .first(page.isFirst())
                .build();
    }

    /**
     * Builds a {@link PagedResponse} directly from a Spring Data {@link Page} of the same type.
     */
    public static <T> PagedResponse<T> of(Page<T> page) {
        return of(page, java.util.function.Function.identity());
    }
}