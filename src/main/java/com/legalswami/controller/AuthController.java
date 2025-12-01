package com.legalswami.controller;

import com.legalswami.dto.GoogleLoginRequest;
import com.legalswami.entity.User;
import com.legalswami.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger log = LoggerFactory.getLogger(AuthController.class);

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleLoginRequest request) {
        try {
            log.info("googleLogin called with email={}, name={}", request.getGemail(), request.getGname());

            if (request.getGemail() == null || request.getGemail().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "email is required"));
            }

            String userId = request.getGemail().toLowerCase();

            Optional<User> existing = userRepository.findById(userId);
            User user = existing.orElseGet(() -> {
                User u = new User();
                u.setId(userId);
                u.setName(request.getGname() == null ? "User" : request.getGname());
                u.setEmail(request.getGemail());
                u.setEmailVerified(true);
                u.setCreatedAt(Instant.now());
                return u;
            });

            user.setLastLogin(Instant.now());
            userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                    "id", user.getId(),
                    "name", user.getName(),
                    "email", user.getEmail(),
                    "emailVerified", user.isEmailVerified()
            ));
        } catch (Exception ex) {
            log.error("Exception in /api/auth/google", ex);
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Internal Server Error",
                    "message", ex.getMessage()
            ));
        }
    }

    @PostMapping("/test/createuser")
    public ResponseEntity<?> createTestUser() {
        try {
            String id = "test-" + UUID.randomUUID();
            User user = new User();
            user.setId(id);
            user.setName("Test User");
            user.setEmail(id + "@example.com");
            user.setEmailVerified(true);
            user.setCreatedAt(Instant.now());
            userRepository.save(user);
            return ResponseEntity.ok(Map.of("ok", true, "id", id));
        } catch (Exception ex) {
            log.error("Exception createTestUser", ex);
            return ResponseEntity.internalServerError().body(Map.of("error", ex.getMessage()));
        }
    }
}
