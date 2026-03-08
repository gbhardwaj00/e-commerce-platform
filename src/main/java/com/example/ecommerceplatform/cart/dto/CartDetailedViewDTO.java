package com.example.ecommerceplatform.cart.dto;

import java.util.List;
import java.util.UUID;

public record CartDetailedViewDTO(
        UUID cartId,
        List<CartItemDetailedDTO> items,
        Integer totalCents,
        String currency
){
}
