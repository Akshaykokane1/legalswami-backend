package com.legalswami.controller;

import com.legalswami.dto.GoogleLoginRequest;
import com.legalswami.dto.RegisterRequest;
import com.legalswami.entity.User;
import com.legalswami.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // for quick testing; restrict in production
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Simple register endpoint.
     * Expects JSON:
     * { "name": "...", "email": "...", "password": "..." }
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (req.getEmail() == null || req.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "email is required"));
        }

        // check if email already exists (repository should provide findByEmail)
        Optional<User> existing = userRepository.findByEmail(req.getEmail().toLowerCase());
        if (existing.isPresent()) {
            return ResponseEntity.status(409).body(Map.of("error", "email_exists"));
        }

        // Create user (simple fields). In production: hash the password
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setName(req.getName() != null ? req.getName() : "User");
        user.setEmail(req.getEmail().toLowerCase());
        user.setPassword(req.getPassword()); // **IMPORTANT:** hash in real app!
        user.setEmailVerified(false);
        user.setCreatedAt(Instant.now());
        user.setLastLogin(Instant.now());

        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "name", user.getName()
        ));
    }

    /**
     * Placeholder Google login endpoint (example).
     * Accepts a GoogleLoginRequest with an "email" field at minimum.
     */
    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleLoginRequest request) {
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "email is required"));
        }

        String email = request.getEmail().toLowerCase();
        Optional<User> maybeUser = userRepository.findByEmail(email);

        User user = maybeUser.orElseGet(() -> {
            // create a new user if not exists
            User u = new User();
            u.setId(UUID.randomUUID().toString());
            u.setName(request.getName() != null ? request.getName() : "GoogleUser");
            u.setEmail(email);
            u.setEmailVerified(true);
            u.setCreatedAt(Instant.now());
            u.setLastLogin(Instant.now());
            return userRepository.save(u);
        });

        // update last login time
        user.setLastLogin(Instant.now());
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "name", user.getName(),
                "emailVerified", user.isEmailVerified()
        ));
    }

}
