package com.basilisk.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Envelope de resposta paginada padrão Basilisk.
 *
 * Uso com Page do Spring Data:
 *   return PageResponse.of(repository.findAll(pageable));
 *
 * Uso manual (ex: query nativa):
 *   return PageResponse.of(items, total, page, limit);
 *
 * Nota: page é 1-indexed na resposta (Spring usa 0-indexed internamente).
 */
@Getter
@AllArgsConstructor
public class PageResponse<T> {

    private List<T> items;
    private long total;
    private int page;
    private int limit;
    private int totalPages;

    public static <T> PageResponse<T> of(Page<T> springPage) {
        return new PageResponse<>(
            springPage.getContent(),
            springPage.getTotalElements(),
            springPage.getNumber() + 1,
            springPage.getSize(),
            springPage.getTotalPages()
        );
    }

    public static <T> PageResponse<T> of(List<T> items, long total, int page, int limit) {
        int totalPages = (int) Math.ceil((double) total / limit);
        return new PageResponse<>(items, total, page, limit, totalPages);
    }
}
