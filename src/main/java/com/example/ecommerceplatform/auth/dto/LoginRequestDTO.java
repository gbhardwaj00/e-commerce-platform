package com.example.ecommerceplatform.auth.dto;

import jakarta.validation.constraints.*;

public record LoginRequestDTO(
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Password is required")
        String password
) {
}
