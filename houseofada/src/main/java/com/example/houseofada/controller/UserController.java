package com.example.houseofada.controller;

import com.example.houseofada.model.AuthRequest;
import com.example.houseofada.model.Product;
import com.example.houseofada.model.User;
import com.example.houseofada.repository.UserRepository;
import com.example.houseofada.security.JwtUtil;
import com.example.houseofada.service.EmailService;
import com.example.houseofada.service.OtpService;
import com.example.houseofada.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final OtpService otpService;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, OtpService otpService,
                          EmailService emailService, UserRepository userRepository,
                          PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.otpService = otpService;
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // ✅ LOGIN
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthRequest request) throws InvalidCredentialsException {
        log.info("Received login request for email: {}", request.getEmail());

        Map<String, Object> response = userService.loginUser(request);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        log.info("Fetching all users");
        return userService.getAllUsersWithCount().getUsers();
    }

    @DeleteMapping("/id/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Request to delete user with ID: {}", userId);
        userService.deleteUserById(userId);
        log.info("user deleted successfully: {}", userId);
    }

    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, String>> sendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = otpService.generateOtp(email);
        emailService.sendOtpEmail(email, otp);

        log.info("OTP for {} is {}", email, otp);

        Map<String, String> response = new HashMap<>();
        response.put("message", "OTP sent successfully to " + email);
        return ResponseEntity.ok(response);
    }


    // ✅ VERIFY OTP & REGISTER USER + RETURN TOKEN
    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, String>> verifyOtpAndRegister(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        String name = request.get("name");
        String password = request.get("password");
        String role = request.get("role");

        log.info("Verifying OTP for email: {}", email);

        // 1️⃣ Verify OTP
        if (!otpService.isOtpValid(email, otp)) {
            log.warn("OTP verification failed for email: {}", email);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid or expired OTP");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // 2️⃣ Check if user already exists
        if (userRepository.existsByEmail(email)) {
            log.warn("User already exists with email: {}", email);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "User already exists with this email");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // 3️⃣ Create and save new user
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        if(role == null || role.isEmpty()) role = "USER";
        user.setRole(role.equalsIgnoreCase("ADMIN") ? "ADMIN" : "USER");


        userRepository.save(user);
        log.info("✅ User {} registered successfully", email);

        // 4️⃣ Generate token for this user
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        log.info("JWT token generated for user {}", user.getEmail());

        // 5️⃣ Return token in JSON response
        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

}
