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
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (req.getEmail() == null || req.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "email is required"));
        }
        Optional<User> existing = userRepository.findByEmail(req.getEmail().toLowerCase());
        if (existing.isPresent()) {
            return ResponseEntity.status(409).body(Map.of("error", "email_exists"));
        }

        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setName(req.getName() != null ? req.getName() : "User");
        user.setEmail(req.getEmail().toLowerCase());
        user.setPassword(req.getPassword()); // hash in real app
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

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleLoginRequest request) {
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "email is required"));
        }

        String email = request.getEmail().toLowerCase();
        Optional<User> maybeUser = userRepository.findByEmail(email);

        User user = maybeUser.orElseGet(() -> {
            User u = new User();
            u.setId(UUID.randomUUID().toString());
            u.setName(request.getName() != null ? request.getName() : "GoogleUser");
            u.setEmail(email);
            u.setEmailVerified(true);
            u.setCreatedAt(Instant.now());
            u.setLastLogin(Instant.now());
            return userRepository.save(u);
        });

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
