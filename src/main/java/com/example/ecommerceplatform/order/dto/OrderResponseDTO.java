package com.example.ecommerceplatform.order.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponseDTO(
        UUID orderId,
        UUID cartId,
        String status,
        String currency,
        Integer totalCents,
        OffsetDateTime createdAt,
        List<OrderItemResponseDTO> items
) {
}
