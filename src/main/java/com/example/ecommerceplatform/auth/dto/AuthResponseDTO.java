package com.example.ecommerceplatform.auth.dto;

import com.example.ecommerceplatform.user.Role;

import java.util.UUID;

public record AuthResponseDTO(
        String token,
        UUID userId,
        String email,
        Role role
) {
}
