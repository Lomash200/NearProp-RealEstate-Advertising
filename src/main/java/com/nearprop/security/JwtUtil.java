package com.nearprop.security;

import com.nearprop.config.JwtConfig;
import com.nearprop.entity.Role;
import com.nearprop.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtil {
    private final JwtConfig jwtConfig;

    // Constants for token expiration times
    private static final long NON_EXPIRING_TOKEN_DURATION = 100L * 365 * 24 * 60 * 60 * 1000; // 100 years in milliseconds
    private static final long STANDARD_TOKEN_DURATION = 10L * 365 * 24 * 60 * 60 * 1000; // 10 years in milliseconds

    public String generateToken(User user, String sessionId) {

        log.info("JWT generation started | userId={} | sessionId={}",
                user.getId(), sessionId);

        // 🎭 Extract roles
        List<String> roles = user.getRoles().stream()
                .map(Role::name)
                .collect(Collectors.toList());

        log.debug("User roles resolved | userId={} | roles={}",
                user.getId(), roles);

        // ⏱️ Determine expiration time based on roles
        long expirationTimeMs = calculateExpirationTime(user.getRoles());

        Date issuedAt = new Date();
        Date expiresAt = new Date(System.currentTimeMillis() + expirationTimeMs);

        log.debug("JWT expiration calculated | userId={} | expirationMs={} | expiresAt={}",
                user.getId(), expirationTimeMs, expiresAt);

        // 🔐 Build JWT
        String token = Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("userId", user.getId().toString())
                .claim("roles", roles)
                .claim("sessionId", sessionId)
                .setIssuedAt(issuedAt)
                .setExpiration(expiresAt)
                .setIssuer("NearpropBackend")
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();

        log.info("JWT generated successfully | userId={} | sessionId={} | tokenLength={}",
                user.getId(), sessionId, getSigningKey());

        return token;
    }

    /**
     * Calculate token expiration time based on user roles
     * - ROLE_USER without other roles: non-expiring token (100 years)
     * - Any other role combination: 7-day token
     */
    private long calculateExpirationTime(Set<Role> roles) {
        // If user has only the USER role and no other roles, use non-expiring token
        if (roles.size() == 1 && roles.contains(Role.USER)) {
            log.debug("Using non-expiring token for ROLE_USER");
            return NON_EXPIRING_TOKEN_DURATION;
        }

        // For any other role combination, use standard token duration
        log.debug("Using standard token duration for roles: {}", roles);
        return STANDARD_TOKEN_DURATION;
    }

    public boolean validateToken(String token) {
        log.info("Validating JWT token");
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            log.info("JWT token validated successfully");
            return true;
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

//    public String getUserIdFromToken(String token) {
//        Claims claims = Jwts.parserBuilder()
//                .setSigningKey(getSigningKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//
//        return claims.getSubject();
//    }

    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("userId", String.class);
    }


    public String getSessionIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("sessionId", String.class);
    }

    public Key getSigningKey() {

        log.debug("Initializing JWT signing key");

        String secret = jwtConfig.getSecret();

        if (secret == null) {
            log.error("JWT secret is NOT configured");
            throw new IllegalStateException("JWT secret is not configured!");
        }

        int originalLength = secret.length();

        // 🔐 Ensure minimum length for HS512
        boolean extended = false;
        if (secret.length() < 32) {
            secret = secret + "_create_by_Sandeep_Kushwaha";
            extended = true;
        }

        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);

        log.info(
                "JWT signing key loaded | originalLength={} | finalLength={} | extended={} | secret={}",
                originalLength,
                keyBytes.length,
                extended,
                secret
        );

        return Keys.hmacShaKeyFor(keyBytes);
    }

} 