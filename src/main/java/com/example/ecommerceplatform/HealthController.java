package com.example.ecommerceplatform;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/")
    public static String home() {
        return "API is running!";
    }

    @GetMapping("/health")
    public static String health() {
        return "OK";
    }
}
