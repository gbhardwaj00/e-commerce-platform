package com.example.ecommerceplatform.catalog.product.dto;

import jakarta.validation.constraints.*;

public record ProductUpdateRequestDTO(
        @NotBlank String title,
        String description,
        @NotNull @Min(0) Integer priceCents,
        @NotBlank @Size(min = 3, max = 3) String currency,
        @NotNull @Min(0) Integer quantityAvailable
) {
}
