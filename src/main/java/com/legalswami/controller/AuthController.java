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
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ----------------------------------------------------------------------
    // 1) Google Login (dummy endpoint)
    // ----------------------------------------------------------------------
    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleLoginRequest request) {

        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "email is required"));
        }

        String userId = request.getEmail().toLowerCase();

        User user = userRepository.findById(userId).orElseGet(() ->
                new User(
                        userId,
                        request.getName() != null ? request.getName() : "User",
                        request.getEmail(),
                        true,
                        Instant.now()
                )
        );

        user.setLastLogin(Instant.now());
        userRepository.save(user);

        return ResponseEntity.ok(
                Map.of(
                        "status", "success",
                        "id", user.getId(),
                        "name", user.getName(),
                        "email", user.getEmail(),
                        "emailVerified", user.isEmailVerified()
                )
        );
    }

    // ----------------------------------------------------------------------
    // 2) REGISTER (This is the endpoint your frontend calls)
    // ----------------------------------------------------------------------
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {

        String name = body.get("name");
        String email = body.get("email");
        String password = body.get("password");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "email is required"));
        }

        // userId = email lowercase
        String userId = email.toLowerCase();

        User newUser = new User(
                userId,
                name,
                email,
                true,
                Instant.now()
        );

        userRepository.save(newUser);

        return ResponseEntity.status(201).body(
                Map.of(
                        "status", "created",
                        "id", newUser.getId(),
                        "name", newUser.getName(),
                        "email", newUser.getEmail()
                )
        );
    }

    // ----------------------------------------------------------------------
    // 3) LOGIN (optional simple login)
    // ----------------------------------------------------------------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {

        String email = body.get("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "email is required"));
        }

        String id = email.toLowerCase();

        return userRepository.findById(id)
                .map(user ->
                        ResponseEntity.ok(
                                Map.of(
                                        "status", "success",
                                        "id", user.getId(),
                                        "name", user.getName(),
                                        "email", user.getEmail()
                                )
                        )
                )
                .orElse(ResponseEntity.status(404).body(Map.of("error", "User not found")));
    }

    // ----------------------------------------------------------------------
    // 4) Test endpoint
    // ----------------------------------------------------------------------
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok(Map.of("status", "working", "time", Instant.now().toString()));
    }
}
