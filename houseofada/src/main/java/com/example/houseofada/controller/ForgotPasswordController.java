package com.example.houseofada.controller;

import com.example.houseofada.model.User;
import com.example.houseofada.repository.UserRepository;
import com.example.houseofada.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ForgotPasswordController {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PasswordEncoder passwordEncoder;

    private static final String OTP_PREFIX = "forgot_password_";

    // ✅ Step 1: Send OTP to Email
    @PostMapping("/forgot-password")
    public ResponseEntity<?> sendResetOtp(@RequestParam String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("No user found with this email!");
        }

        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

        // save OTP in Redis with expiry (10 minutes)
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        ops.set(OTP_PREFIX + email, otp, Duration.ofMinutes(10));

        // send email
        emailService.sendForgotPasswordOtp(email, otp, "Otp is valid for 10 min");


        return ResponseEntity.ok("OTP sent successfully to your registered email.");
    }

    // ✅ Step 2: Verify OTP and Reset Password
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String email,
                                           @RequestParam String otp,
                                           @RequestParam String newPassword) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        String redisKey = OTP_PREFIX + email;
        Object storedOtp = ops.get(redisKey);

        if (storedOtp == null) {
            return ResponseEntity.badRequest().body("OTP expired or not found!");
        }

        if (!storedOtp.toString().equals(otp)) {
            return ResponseEntity.badRequest().body("Invalid OTP!");
        }

        User user = userRepository.findByEmail(email).orElseThrow();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // delete OTP after successful password reset
        redisTemplate.delete(redisKey);

        return ResponseEntity.ok("Password reset successful!");
    }
}
