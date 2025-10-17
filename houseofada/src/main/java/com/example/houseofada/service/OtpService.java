package com.example.houseofada.service;

import com.example.houseofada.model.User;
import com.example.houseofada.repository.UserRepository;
import com.example.houseofada.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
public class OtpService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;

    public OtpService(RedisTemplate<String, String> redisTemplate,
                      JwtUtil jwtUtil,
                      UserRepository userRepository) {
        this.redisTemplate = redisTemplate;
        this.jwtUtil = jwtUtil;
    }

    // ✅ Generate a new OTP and store it in Redis for 5 minutes
    public String generateOtp(String email) {
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        redisTemplate.opsForValue().set("OTP:" + email, otp, Duration.ofMinutes(5));
        return otp;
    }

    // ✅ Verify OTP validity
    public boolean isOtpValid(String email, String otp) {
        String storedOtp = redisTemplate.opsForValue().get("OTP:" + email);

        if (storedOtp != null && storedOtp.equals(otp)) {
            redisTemplate.delete("OTP:" + email); // remove after success
            return true;
        }
        return false;
    }

    // ✅ Generate JWT token for a newly registered user
    public String generateTokenForUser(User user) {
        return jwtUtil.generateToken(user.getEmail(), user.getRole());
    }
}
