package com.example.ecommerceplatform.auth;

import com.example.ecommerceplatform.user.User;
import com.example.ecommerceplatform.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {
    private final UserRepository userRepository;

    public  CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalStateException("No authenticated user found");
        }
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->  new IllegalArgumentException("Authenticated user not found"));
    }
}
