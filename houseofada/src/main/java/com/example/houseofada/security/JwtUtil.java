package com.example.houseofada.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    private static final String SECRET = "houseofadaSecretKey1234567890123456";
    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());


    public String generateToken(String email, String role) {
        // Token expiration time in milliseconds (10 hours)
        long EXPIRATION_MS = 1000 * 60 * 60 * 10;
        return Jwts.builder()
                .setSubject(email)               // Set the "subject" of the token to the user's email
                .claim("role", role)             // Add custom claim "role" to store user/admin role
                .setIssuedAt(new Date())         // Token issue time (current time)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS)) // Expiry time
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // Sign token with secret key
                .compact();                      // Build the token and return as string
    }

    public String extractEmail(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)       // Use the same secret key to parse
                .parseClaimsJws(token)           // Parse the JWT
                .getBody()                       // Get payload (claims)
                .getSubject();                   // Return the subject (email)
    }
    public String extractRole(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);       // Get the "role" claim
    }

    public boolean validateToken(String token, String email) {
        return extractEmail(token).equals(email) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }

}
