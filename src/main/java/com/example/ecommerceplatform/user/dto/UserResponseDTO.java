package com.example.ecommerceplatform.user.dto;

import com.example.ecommerceplatform.user.Role;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String email,
        Role role,
        OffsetDateTime createdAt
) {
}
