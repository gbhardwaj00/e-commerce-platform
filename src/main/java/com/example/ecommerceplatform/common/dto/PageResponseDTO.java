package com.example.ecommerceplatform.common.dto;

import java.util.List;

public record PageResponseDTO<T>(
        List<T> items,
        int page,
        int size,
        long totalItems,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious,
        List<String> sort
) { }
