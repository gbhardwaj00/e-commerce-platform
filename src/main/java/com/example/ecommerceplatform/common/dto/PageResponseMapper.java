package com.example.ecommerceplatform.common.dto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

public final class PageResponseMapper {
    private PageResponseMapper() {}

    public static <T> PageResponseDTO<T> toDTO(Page<T> page) {
        List<String> sort = page.getSort().stream().map(PageResponseMapper::formatOrder).toList();

        return new PageResponseDTO<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious(),
                sort
        );
    }

    private static String formatOrder(Sort.Order o) {
        return o.getProperty() + ":" + o.getDirection().name().toLowerCase();
    }
}
