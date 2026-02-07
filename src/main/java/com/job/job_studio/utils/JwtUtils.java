package com.job.job_studio.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    // 密钥 (生产环境应放在配置文件中，且更复杂)
    private static final String JWT_SECRET = "JobStudioSecretKeyForJwtSignatureShouldBeLongEnough123456";
    // 过期时间：24小时
    private static final int JWT_EXPIRATION_MS = 86400000;

    private Key key() {
        return Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
    }

    // 生成 Token
    public String generateJwtToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + JWT_EXPIRATION_MS))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 从 Token 获取用户名
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    // 校验 Token
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException e) {
            System.err.println("JWT Token 无效: " + e.getMessage());
        }
        return false;
    }
}