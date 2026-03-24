package com.example.ecommerceplatform.auth;

import com.example.ecommerceplatform.auth.dto.AuthResponseDTO;
import com.example.ecommerceplatform.auth.dto.LoginRequestDTO;
import com.example.ecommerceplatform.auth.dto.RegisterRequestDTO;
import com.example.ecommerceplatform.user.dto.UserResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO register(@RequestBody @Valid RegisterRequestDTO dto) {
        return authService.register(dto);
    }

    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody @Valid LoginRequestDTO dto) {
        return authService.login(dto);
    }
}
