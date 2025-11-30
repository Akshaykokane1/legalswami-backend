package com.legalswami.controller;

import com.legalswami.dto.GoogleLoginRequest;
import com.legalswami.entity.User;
import com.legalswami.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Very simple placeholder Google login endpoint.
     *
     * IMPORTANT:
     *  - This does NOT verify the Google ID token cryptographically.
     *  - For production, you MUST verify the token with Google's public keys.
     */
    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleLoginRequest request) {
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
        }

        String userId = request.getEmail().toLowerCase();

        User user = userRepository.findById(userId).orElseGet(() ->
            new User(userId,
                    request.getName() != null ? request.getName() : "User",
                    request.getEmail(),
                    true,
                    Instant.now())
        );

        user.setLastLogin(Instant.now());
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "emailVerified", user.isEmailVerified()
        ));
    }

    /**
     * Simple test endpoint to create a dummy user.
     */
    @PostMapping("/test/createUser")
    public ResponseEntity<?> createTestUser() {
        String id = "test-" + UUID.randomUUID();
        User user = new User(id, "Test User", "test@example.com", true, Instant.now());
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("ok", true, "id", id));
    }
}
