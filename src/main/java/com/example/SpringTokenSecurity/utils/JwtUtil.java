package com.example.SpringTokenSecurity.utils;

import com.example.SpringTokenSecurity.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Component
public class JwtUtil {
    private final SecretKey key;

    public JwtUtil() throws NoSuchAlgorithmException {
        // Generate a new key for HMAC-SHA256
        this.key = KeyGeneratorUtil.generateKey();
    }

    public long getExpirationTime() {
        // Assuming you set a fixed expiration time for your tokens
        return 1 * 60000;// Set expiration to 1 minute (1 * 60,000 ms)
    }

    public String generateToken(User user) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + getExpirationTime());  // Set expiration to 1 minute

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("firstName", user.getFirstName())
                .claim("lastName", user.getLastName())
                .claim("username", user.getUsername())
                .claim("role", user.getRole())
                .issuedAt(now)
                .expiration(expirationDate) // 1 hour expiry
                .signWith(key)
                .compact();
    }

    // Validate the token
    public boolean validateToken(String jwt) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // Handle token validation exceptions
            return false;
        }
    }

    public User extractUserDetails(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload(); // Get the claims from the token

        User user = new User();
        user.setFirstName(claims.get("firstName", String.class));
        user.setLastName(claims.get("lastName", String.class));
        user.setUsername(claims.getSubject());
        user.setRole(claims.get("role", String.class));

        return user;
    }
}
