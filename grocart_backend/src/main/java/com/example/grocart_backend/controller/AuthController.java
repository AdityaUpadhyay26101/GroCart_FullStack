package com.example.grocart_backend.controller;

import com.example.grocart_backend.model.User;
import com.example.grocart_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {

        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Username is required!");
        }


        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body("Error: Username already taken!");
        }


        User savedUser = userRepository.save(user);
        return ResponseEntity.ok("User '" + savedUser.getUsername() + "' registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {

        Optional<User> userOptional = userRepository.findByUsername(user.getUsername());


        if (userOptional.isPresent()) {


            User dbUser = userOptional.get();


            if (dbUser.getPassword().equals(user.getPassword())) {


                System.out.println("Login Successful for user: " + dbUser.getUsername());
                return ResponseEntity.ok(dbUser);

            } else {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Wrong Password");
            }
        }


        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: User Not Found!");
    }
}