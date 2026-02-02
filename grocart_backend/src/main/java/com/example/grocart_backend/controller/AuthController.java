package com.example.grocart_backend.controller;

import com.example.grocart_backend.model.User;
import com.example.grocart_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Import this
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // Inject the BCrypt bean we defined in SecurityConfig
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {

        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Username is required!");
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body("Error: Username already taken!");
        }

        // HASH the password before saving to MySQL
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(Collections.singletonMap("message", "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {

        Optional<User> userOptional = userRepository.findByUsername(user.getUsername());

        if (userOptional.isPresent()) {
            User dbUser = userOptional.get();

            // USE matches() to compare raw password with the hash in DB
            if (passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
                System.out.println("Login Successful for user: " + dbUser.getUsername());
                return ResponseEntity.ok(dbUser);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Wrong Password");
            }
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: User Not Found!");
    }
}