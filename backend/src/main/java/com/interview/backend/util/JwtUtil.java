package com.interview.backend.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")          // 从 application.yml 读取密钥
    private String secret;

    @Value("${jwt.expiration}")      // 过期时间（毫秒）
    private long expiration;

    // 获取加密密钥
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // 生成 Token
    public String generateToken(String username) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
            .subject(username)            // 数据：用户名
            .issuedAt(now)                // 签发时间
            .expiration(expireDate)       // 过期时间
            .signWith(getKey())           // 签名（防伪造）
            .compact();                   // 生成字符串
    }

    // 从 Token 里取出用户名
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
            .verifyWith(getKey())         // 用同一个密钥验证
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }

    // 验证 Token 是否有效
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;  // 过期、签名不对、格式不对都算无效
        }
    }
}
