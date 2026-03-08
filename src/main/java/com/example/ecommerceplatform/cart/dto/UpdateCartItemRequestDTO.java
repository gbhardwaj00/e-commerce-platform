package com.example.ecommerceplatform.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateCartItemRequestDTO(
        @NotNull @Min(1) Integer newQuantity
) {
}
