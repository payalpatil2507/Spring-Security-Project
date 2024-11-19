package com.example.SpringTokenSecurity.utils;

import com.example.SpringTokenSecurity.dto.CustomUser;
import com.example.SpringTokenSecurity.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Component
public class JwtTokenUtil {
    private final SecretKey key;
    // Key for roles in the JWT claims
    private static final String CLAIM_KEY_ROLES = "role";

    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        // Extract roles from the claim or return an empty list if not found
        Object rolesClaim = claims.get(CLAIM_KEY_ROLES);
        if (rolesClaim instanceof List) {
            return (List<String>) rolesClaim;
        }
        // If rolesClaim is a string (e.g., single role), wrap it into a list
        if (rolesClaim instanceof String) {
            return Collections.singletonList((String) rolesClaim);
        }
        return Collections.emptyList();  // Default to empty if roles are not present
    }


    public JwtTokenUtil() throws NoSuchAlgorithmException {
        this.key = KeyGeneratorUtil.generateKey(); // Generate a new key for HMAC-SHA256
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public long getExpirationTime() {
        return 50 * 60000;// Set expiration to 1 minute (1 * 60,000 ms)
    }

    public String generateToken(CustomUser user) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + getExpirationTime());  // Set expiration to 50 minute

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
