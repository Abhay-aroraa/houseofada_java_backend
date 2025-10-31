package com.example.houseofada.service;

import com.example.houseofada.dto.UserResponse;
import com.example.houseofada.exception.UserNotFoundException;
import com.example.houseofada.model.AuthRequest;
import com.example.houseofada.model.Product;
import com.example.houseofada.model.User;
import com.example.houseofada.repository.UserRepository;
import com.example.houseofada.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    public UserResponse getAllUsersWithCount() {
        List<User> users = userRepository.findAll();
        long count = userRepository.count(); // gets total number of users
        return new UserResponse(users, count);
    }

    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("user not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    public Map<String, Object> registerUser(User user) throws InvalidCredentialsException {
        log.info("Attempting to register user with email: {}", user.getEmail());

        if (userRepository.existsByEmail(user.getEmail())) {
            log.error("Registration failed: Email {} already exists", user.getEmail());
            throw new InvalidCredentialsException("Email already exists");
        }

        // Hash the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set default role if not provided
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }

        // Save the user
        User savedUser = userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getRole());

        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("email", savedUser.getEmail());
        response.put("role", savedUser.getRole());
        response.put("userId", savedUser.getId());
        response.put("message", "User registered successfully");

        log.info("User {} registered successfully with role {}", savedUser.getEmail(), savedUser.getRole());
        return response;
    }



    public Map<String, Object> loginUser(AuthRequest request) throws InvalidCredentialsException {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException(" Incorrect password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("email", user.getEmail());
        response.put("role", user.getRole());
        response.put("userId", user.getId());
        response.put("message", "Login successful");
        return response;
    }


}
