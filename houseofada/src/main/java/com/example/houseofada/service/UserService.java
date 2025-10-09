package com.example.houseofada.service;

import com.example.houseofada.model.AuthRequest;
import com.example.houseofada.model.User;
import com.example.houseofada.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder(); // for hashing passwords
    }

    // REGISTER USER
    public String registerUser(User user) {
        log.info("Attempting to register user with email: {}", user.getEmail());

        if (userRepository.existsByEmail(user.getEmail())) {
            log.error("Registration failed: Email {} already exists", user.getEmail());
            throw new RuntimeException("Email already exists");
        }

        // Hash the password before saving
        log.info(user.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));


        userRepository.save(user);
        log.info("User {} registered successfully", user.getEmail());
        return "User registered successfully";
    }

    // LOGIN USER
    public String loginUser(AuthRequest request) {
        log.info("Attempting to log in user: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.error("Login failed: User {} not found", request.getEmail());
                    return new RuntimeException("User not found");
                });



        // Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.error("Login failed: Incorrect password for {}", request.getEmail());
            throw new RuntimeException("Invalid credentials");
        }

        log.info("User {} logged in successfully", request.getEmail());
        // Here you can generate a JWT token if needed
        return "Login successful";
    }

    // LOGIN ADMIN
    public String loginAdmin(AuthRequest request) {
        log.info("Attempting to log in admin: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.error("Login failed: Admin {} not found", request.getEmail());
                    return new RuntimeException("Admin not found");
                });

        // Check role
        if (!user.getRole().equalsIgnoreCase("ADMIN")) {
            log.error("Login failed: User {} is not an admin", request.getEmail());
            throw new RuntimeException("Not authorized");
        }

        // Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.error("Login failed: Incorrect password for ADMIN {}", request.getEmail());
            throw new RuntimeException("Invalid credentials");
        }

        log.info("Admin {} logged in successfully", request.getEmail());
        // Here you can generate a JWT token if needed
        return "Admin login successful";
    }
}
