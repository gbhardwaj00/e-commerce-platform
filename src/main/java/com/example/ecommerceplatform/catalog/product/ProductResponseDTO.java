package com.example.ecommerceplatform.catalog.product;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ProductResponseDTO(
        UUID id,
        String title,
        String description,
        Integer priceCents,
        String currency,
        Integer quantityAvailable,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
