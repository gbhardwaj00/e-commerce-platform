package com.example.ecommerceplatform.catalog.product;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

public class ProductMapper {
    private ProductMapper() {}

    public static @NotNull Product toEntity(@NotNull ProductCreateRequestDTO dto) {
        Product p = new Product();
        p.setId(UUID.randomUUID());
        p.setTitle(dto.title());
        p.setDescription(dto.description());
        p.setPriceCents(dto.priceCents());
        p.setCurrency(normalizeCurrency(dto.currency()));
        p.setQuantityAvailable(dto.quantityAvailable() == null ? 0 : dto.quantityAvailable());

        OffsetDateTime now = OffsetDateTime.now();
        p.setCreatedAt(now);
        p.setUpdatedAt(now);

        return p;
    }

    public static ProductResponseDTO toDTO(@NotNull Product p) {
        return new ProductResponseDTO(
                p.getId(),
                p.getTitle(),
                p.getDescription(),
                p.getPriceCents(),
                p.getCurrency(),
                p.getQuantityAvailable(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }

    public static void applyUpdate(Product existing, ProductUpdateRequestDTO dto) {
        existing.setTitle(dto.title());
        existing.setDescription(dto.description());
        existing.setPriceCents(dto.priceCents());
        existing.setCurrency(normalizeCurrency(dto.currency()));
        existing.setQuantityAvailable(dto.quantityAvailable());
        existing.setUpdatedAt(OffsetDateTime.now());
    }

    private static String normalizeCurrency(String currency) {
        return currency == null ? null : currency.trim().toUpperCase();
    }
}
