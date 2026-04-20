package com.anas.StudentRestApis.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JwtTokenProvider - Generate, validate, and extract claims from JWT tokens
 *
 * Design Notes:
 * - Symmetric signing (HS512) - same secret for signing and verification
 * - Claims include: username, userId, role, email, and standard claims (iat,
 * exp)
 * - Token validation checks signature and expiration
 * - Exceptions are caught and logged (never throw to caller)
 */
@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private String jwtExpiration;

    @Value("${jwt.refresh.expiration}")
    private String jwtRefreshExpiration;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.audience}")
    private String audience;

    // ==================== Token Generation ====================

    /**
     * Generate JWT token for user
     *
     * @param username User's username
     * @param userId   User's ID
     * @param role     User's role
     * @param email    User's email
     * @return Signed JWT token
     */
    public String generateToken(String username, Long userId, String role, String email) {
        return buildToken(username, userId, role, email, Long.parseLong(jwtExpiration));
    }

    /**
     * Generate refresh token with longer expiration
     *
     * @param username User's username
     * @param userId   User's ID
     * @return Signed refresh JWT token
     */
    public String generateRefreshToken(String username, Long userId) {
        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("type", "refresh")
                .issuer(issuer)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + Long.parseLong(jwtRefreshExpiration)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Internal method to build token with claims
     */
    private String buildToken(String username, Long userId, String role, String email, long expirationTime) {
        long now = System.currentTimeMillis();
        long expiryDate = now + expirationTime;

        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("role", role)
                .claim("email", email)
                .issuer(issuer)
                .audience().add(audience).and()
                .issuedAt(new Date(now))
                .expiration(new Date(expiryDate))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    // ==================== Token Validation ====================

    /**
     * Validate JWT token
     *
     * Checks:
     * 1. Signature is valid (not tampered)
     * 2. Token is not expired
     * 3. Expected claims are present
     *
     * @param token JWT token to validate
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            log.info("JWT token validated successfully");
            return true;
        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
            return false;

        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;

        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token: {}", e.getMessage());
            return false;

        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token: {}", e.getMessage());
            return false;

        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty: {}", e.getMessage());
            return false;

        } catch (JwtException e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            log.warn("Error checking token expiration: {}", e.getMessage());
            return true; // Assume expired on error
        }
    }

    /**
     * Get time remaining until token expiration (in seconds)
     */
    public long getTimeUntilExpiration(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return (claims.getExpiration().getTime() - System.currentTimeMillis()) - 1000;
        } catch (Exception e) {
            log.warn("Error getting token expiration time: {}", e.getMessage());
            return -1;
        }
    }

    // ==================== Claims Extraction ====================

    /**
     * Extract username from token
     */
    public String getUsernameFromToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    /**
     * Extract userId from token
     */
    public Long getUserIdFromToken(String token) {
        return getAllClaimsFromToken(token).get("userId", Long.class);
    }

    /**
     * Extract role from token
     */
    public String getRoleFromToken(String token) {
        return getAllClaimsFromToken(token).get("role", String.class);
    }

    /**
     * Extract email from token
     */
    public String getEmailFromToken(String token) {
        return getAllClaimsFromToken(token).get("email", String.class);
    }

    /**
     * Extract all claims from token
     *
     * NOTE: This will throw ExpiredJwtException if token is expired
     * Use validateToken() first to check expiration safely
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getPayload();
    }

    // ==================== Token Utilities ====================

    /**
     * Get signing key from secret
     *
     * HMAC256 requires minimum 256 bits (32 bytes)
     * HS512 requires minimum 512 bits (64 bytes)
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes();
        if (keyBytes.length < 64) {
            log.warn("JWT secret is less than 64 bytes ({}). HS512 requires minimum 512 bits (64 bytes)!",
                    keyBytes.length);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extract JWT token from Bearer string
     *
     * Expected format: "Bearer <token>"
     */
    public String extractTokenFromBearer(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * Get token expiration time as Date
     */
    public Date getTokenExpirationDate(String token) {
        try {
            return getAllClaimsFromToken(token).getExpiration();
        } catch (Exception e) {
            log.warn("Could not get token expiration date: {}", e.getMessage());
            return null;
        }
    }
}
