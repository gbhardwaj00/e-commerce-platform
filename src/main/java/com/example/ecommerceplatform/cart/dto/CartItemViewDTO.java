package com.example.ecommerceplatform.cart.dto;

import java.util.UUID;

public record CartItemViewDTO(
        UUID productId,
        Integer quantity
) {
}
