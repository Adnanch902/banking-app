package com.example.bankingapp.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(Authentication auth) {
        String username = auth.getName();
        return Jwts.builder()
                .subject(username)
                .claim("roles", auth.getAuthorities())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key())
                .compact();
    }

    public String getUsername(String token) {
        return Jwts.parser()                // ✅ new API in 0.12.x
                .verifyWith(key())          // replaces setSigningKey()
                .build()
                .parseSignedClaims(token)   // replaces parseClaimsJws()
                .getPayload()
                .getSubject();
    }

    public boolean validate(String token, String username) {
        try {
            Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parseSignedClaims(token);
            return getUsername(token).equals(username);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
