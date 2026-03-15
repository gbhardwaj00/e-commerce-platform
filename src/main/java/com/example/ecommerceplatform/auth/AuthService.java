package com.example.ecommerceplatform.auth;

import com.example.ecommerceplatform.user.Role;
import com.example.ecommerceplatform.user.User;
import com.example.ecommerceplatform.user.UserRepository;
import com.example.ecommerceplatform.user.dto.RegisterRequestDTO;
import com.example.ecommerceplatform.user.dto.UserResponseDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

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
}
