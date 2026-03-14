package com.example.ecommerceplatform.order.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

// used for Paginated Order response
public record OrderSummaryDTO(
        UUID orderId,
        UUID cartId,
        String status,
        String currency,
        Integer totalCents,
        OffsetDateTime createdAt
) {
}
