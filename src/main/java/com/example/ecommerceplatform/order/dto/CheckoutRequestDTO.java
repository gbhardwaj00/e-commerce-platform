package com.example.ecommerceplatform.order.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CheckoutRequestDTO(
        @NotNull UUID cartId
) {}
