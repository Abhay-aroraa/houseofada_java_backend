package com.example.houseofada.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;


    public void sendOtpEmail(String to, String otp) {
        try {
            log.info("Sending OTP email to: {}", to);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Your OTP Code - House of Ada");
            message.setText("Your OTP for registration is: " + otp + "\n\nThis code will expire in 5 minutes.");
            mailSender.send(message);
            log.info("OTP email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send OTP email to: {}", to, e);
        }
    }
    public void sendForgotPasswordOtp(String to, String otp, String s) {
        try {
            log.info("Sending forgot-password OTP email to: {}", to);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Reset Your Password - House of Ada");
            message.setText("Your OTP for password reset is: " + otp
                    + "\n\nThis OTP is valid for 10 minutes."
                    + "\nIf you didn’t request a password reset, please ignore this email.");
            mailSender.send(message);
            log.info("Forgot-password OTP email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send forgot-password OTP email to: {}", to, e);
        }
    }
}
