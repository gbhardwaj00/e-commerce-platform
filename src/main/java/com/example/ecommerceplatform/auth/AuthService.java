package com.example.ecommerceplatform.auth;

import com.example.ecommerceplatform.auth.dto.AuthResponseDTO;
import com.example.ecommerceplatform.auth.dto.LoginRequestDTO;
import com.example.ecommerceplatform.user.Role;
import com.example.ecommerceplatform.user.User;
import com.example.ecommerceplatform.user.UserRepository;
import com.example.ecommerceplatform.auth.dto.RegisterRequestDTO;
import com.example.ecommerceplatform.common.AuthenticationFailedException;
import com.example.ecommerceplatform.user.dto.UserResponseDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepo, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public UserResponseDTO register(RegisterRequestDTO dto) {
        String normalizedEmail = dto.email().trim().toLowerCase();

        if(userRepo.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Email is already in use");
        }

        OffsetDateTime now = OffsetDateTime.now();

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(dto.password()));
        user.setRole(Role.USER);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        userRepo.save(user);

        return new UserResponseDTO(
          user.getId(),
          user.getEmail(),
          user.getRole(),
          user.getCreatedAt()
        );
    }

    @Transactional
    public AuthResponseDTO login(LoginRequestDTO dto) {
        String normalizeEmail = dto.email().trim().toLowerCase();

        User user = userRepo.findByEmail(normalizeEmail)
                .orElseThrow(() -> new AuthenticationFailedException("Invalid email or password"));

        boolean matches = passwordEncoder.matches(dto.password(), user.getPasswordHash());

        if (!matches) {
            throw new AuthenticationFailedException("Invalid email or password");
        }

        return new AuthResponseDTO(
                jwtService.generateToken(user),
                user.getId(),
                user.getEmail(),
                user.getRole()
        );

    }
}
