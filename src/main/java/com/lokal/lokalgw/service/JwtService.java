package com.lokal.lokalgw.service;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    private final String SECRET_KEY = "yoursecretkedfdsfjjcdjcsdl3423Kkemsamdsdkskds223kkkkksds33y"; // Keep this key secure and change it in production
    private final long ACCESS_TOKEN_EXPIRATION_TIME = 15 * 60 * 1000; // 15 minutes
    private final long REFRESH_TOKEN_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000; // 7 days

    // Map to store refresh tokens and their associated usernames (or use a database for persistence)
    private final Map<String, String> refreshTokens = new HashMap<>();

    public String generateAccessToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, ACCESS_TOKEN_EXPIRATION_TIME);
    }

    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, REFRESH_TOKEN_EXPIRATION_TIME);
    }

    private String createToken(Map<String, Object> claims, String subject, long expirationTime) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            // Token is expired
            return false;
        } catch (Exception e) {
            // Token is invalid
            return false;
        }
    }

    public String extractUsername(String token) {
        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public void storeRefreshToken(String refreshToken, String username) {
        refreshTokens.put(refreshToken, username);
    }

    public String getUsernameFromRefreshToken(String refreshToken) {
        return refreshTokens.get(refreshToken);
    }

    public void invalidateRefreshToken(String refreshToken) {
        refreshTokens.remove(refreshToken);
    }
}