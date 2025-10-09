package com.example.houseofada.controller;

import com.example.houseofada.model.AuthRequest;
import com.example.houseofada.model.User;
import com.example.houseofada.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // User registration
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        log.info("Received registration request for email: {}", user.getEmail());
        String result = userService.registerUser(user);
        return ResponseEntity.ok(result);
    }

    // User login
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest request) {
        log.info("Received login request for email: {}", request.getEmail());
        String token = userService.loginUser(request);
        return ResponseEntity.ok(token);
    }

    // Admin login
    @PostMapping("/admin/login")
    public ResponseEntity<String> adminLogin(@RequestBody AuthRequest request) {
        log.info("Received ADMIN login request for email: {}", request.getEmail());
        String token = userService.loginAdmin(request);
        return ResponseEntity.ok(token);
    }
}
