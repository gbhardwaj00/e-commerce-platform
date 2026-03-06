package com.example.ecommerceplatform.common.dto;

import java.time.OffsetDateTime;
import java.util.Map;

public record ErrorResponseDTO(
        OffsetDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fieldErrors
) {
}
