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
}
