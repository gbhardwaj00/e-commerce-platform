package com.example.ecommerceplatform.cart.dto;

import java.util.UUID;

public record CartItemDetailedDTO(
        UUID productId,
        String title,
        Integer priceCents,
        String currency,
        Integer quantity,
        Integer lineTotalCents
) {
}
