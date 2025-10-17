package com.example.houseofada.service;

import com.example.houseofada.model.AuthRequest;
import com.example.houseofada.model.User;
import com.example.houseofada.repository.UserRepository;
import com.example.houseofada.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // Constructor injection
    public UserService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

  public String registerUser(User user) {
        log.info("Attempting to register user with email: {}", user.getEmail());

        if (userRepository.existsByEmail(user.getEmail())) {
            log.error("Registration failed: Email {} already exists", user.getEmail());
            throw new RuntimeException("Email already exists");
        }

        // Hash the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));


        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }

        userRepository.save(user);

        log.info("User {} registered successfully with role {}", user.getEmail(), user.getRole());
        return "User registered successfully";
    }

    // ------------------ LOGIN USER ------------------
    public String loginUser(AuthRequest request) {
        log.info("Attempting to log in user: {}", request.getEmail());

        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.error("Login failed: User {} not found", request.getEmail());
                    return new RuntimeException("User not found");
                });

        // Validate password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.error("Login failed: Incorrect password for {}", request.getEmail());
            throw new RuntimeException("Invalid credentials");
        }

        log.info("User {} authenticated successfully with role {}", user.getEmail(), user.getRole());

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        log.info("JWT token generated for user {}", user.getEmail());
        return token;
    }
}
