package com.wenziyue.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Base64;
import java.util.Date;

/**
 * @author wenziyue
 */
@Component
public class JwtUtils {
    private final String secretKey = "wenziyue-secret-wenziyue-secret-wenziyue-secret";
    String encodedKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    private final long expiration = 86400000L; // 1天

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, encodedKey)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser().setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername());
    }
}
