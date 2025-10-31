package com.example.houseofada.service;

import com.example.houseofada.exception.UserNotFoundException;
import com.example.houseofada.model.User;
import com.example.houseofada.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForgotPasswordService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final RedisTemplate<String, Object> redisTemplate;
   private  final  PasswordEncoder passwordEncoder;

    private static final String OTP_PREFIX = "forgot_otp:";

    // Step 1: Send OTP
    public void sendForgotOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        String otp = String.valueOf(100000 + new Random().nextInt(900000)); // 6-digit OTP

        // Save OTP to Redis (expire in 5 minutes)
        redisTemplate.opsForValue().set(OTP_PREFIX + email, otp, 5, TimeUnit.MINUTES);

        // Send OTP via email
        emailService.sendOtpEmail(email, otp);
        log.info("Forgot password OTP sent to {}", email);
    }

    // Step 2: Verify OTP
    public boolean verifyForgotOtp(String email, String otp) {
        String storedOtp = (String) redisTemplate.opsForValue().get(OTP_PREFIX + email);
        if (storedOtp == null || !storedOtp.equals(otp)) {
            return false;
        }
        // OTP valid → remove it
        redisTemplate.delete(OTP_PREFIX + email);
        return true;
    }

    // Step 3: Reset Password
    public void resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password reset successful for {}", email);
    }
}
