package com.example.houseofada.security;

import com.example.houseofada.model.User;
import com.example.houseofada.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                email = jwtUtil.extractUsername(token);
            } catch (Exception e) {
                log.error("JWT extraction failed: {}", e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                return;
            }
        }

        // Only set authentication if not already set and token is valid
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isEmpty()) {
                log.warn("User not found for email: {}", email);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
                return;
            }

            User user = userOptional.get();

            // You can create your own UserDetails implementation if needed
            UserDetails userDetails = org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password(user.getPassword()) // not used but required by interface
                    .roles(user.getRole()) // like ADMIN or USER
                    .build();

            if (jwtUtil.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // ✅ This tells Spring Security the user is authenticated
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("User authenticated: {}", email);
            } else {
                log.warn("JWT validation failed for user: {}", email);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
