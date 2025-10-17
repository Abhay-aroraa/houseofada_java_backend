package com.example.houseofada.controller;

import com.example.houseofada.model.User;
import com.example.houseofada.repository.UserRepository;
import com.example.houseofada.service.EmailService;
import com.example.houseofada.service.OtpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
 @Slf4j
 @CrossOrigin
 @RestController
 @RequestMapping("/api")
public class OtpController {

    private  final UserRepository userRepository;

    private final OtpService otpService;
    private final EmailService emailService;

    public OtpController(UserRepository userRepository, OtpService otpService, EmailService emailService) {
        this.userRepository = userRepository;
        this.otpService = otpService;
        this.emailService = emailService;
    }

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestParam String email) {
        try {
            log.info("Request to send OTP for email: {}", email);
            Optional<User> user = userRepository.findByEmail(email);

            if (user.isEmpty()) {
                log.warn("User not found for email: {}", email);
                return ResponseEntity.badRequest().body("User not found");
            }

            String otp = otpService.generateOtp(email);
            emailService.sendOtpEmail(email, otp);
            log.info("OTP sent successfully to email: {}", email);
            return ResponseEntity.ok("OTP sent successfully");
        } catch (Exception ex) {
            log.error("Error sending OTP to email: {}", email, ex);
            return ResponseEntity.internalServerError().body("Failed to send OTP");
        }
    }

}
