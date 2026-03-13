package com.example.ecommerceplatform.order.dto;

import java.util.UUID;

public record OrderItemResponseDTO(
        UUID productId,
        String productTitle,
        Integer unitPriceCents,
        String currency,
        Integer quantity,
        Integer lineTotalCents
) {}
