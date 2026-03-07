package com.example.ecommerceplatform.cart.dto;

import java.util.List;
import java.util.UUID;

public record CartViewDTO(
        UUID cartId,
        List<CartItemViewDTO> items
) {
}
