package com.wenziyue.security.utils;

import com.wenziyue.security.properties.SecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author wenziyue
 */
@NoArgsConstructor
@AllArgsConstructor
public class JwtUtils {

    private SecurityProperties properties;

    public String generateToken(String userId) {
        return generateToken(userId, properties.getExpire(), properties.getJwtSecret());
    }

    /**
     * 生成 token（传入用户 ID 或用户名等信息）
     */
    public String generateToken(String userId, long expiration, String secret) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    /**
     * 解析 token，获取用户 ID（也可以改成返回完整 Claims）
     */
    public String getUserIdFromToken(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * 校验 token 是否有效（是否过期）
     */
    public boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(properties.getJwtSecret())
                .parseClaimsJws(token)
                .getBody();
    }

    public long getExpirationRemaining(String token) {
        return getClaims(token).getExpiration().getTime() - System.currentTimeMillis();
    }

}
