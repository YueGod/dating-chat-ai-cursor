package com.dating.ai.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * 用于生成和验证JWT令牌，支持访问令牌和刷新令牌
 *
 * @author dating-ai
 */
@Component
@Slf4j
public class JwtUtils {

    /**
     * JWT密钥，用于签名和验证
     */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * 访问令牌有效期（秒）
     */
    @Value("${jwt.expiration}")
    private int jwtExpiration;

    /**
     * 刷新令牌有效期（秒）
     */
    @Value("${jwt.refresh-expiration}")
    private int refreshExpiration;

    /**
     * 获取签名密钥
     * 
     * @return 签名用的密钥对象
     */
    private Key getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成访问令牌
     * 
     * @param userId 用户ID
     * @return JWT令牌字符串
     */
    public String generateToken(String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpiration * 1000L))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 生成刷新令牌
     * 
     * @param userId 用户ID
     * @return JWT刷新令牌字符串
     */
    public String generateRefreshToken(String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("isRefreshToken", true);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + refreshExpiration * 1000L))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 从令牌中提取用户ID
     * 
     * @param token JWT令牌
     * @return 用户ID
     */
    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("userId", String.class);
    }

    /**
     * 验证令牌是否有效
     * 
     * @param token JWT令牌
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
        } catch (SignatureException e) {
            log.error("无效的JWT签名: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("无效的JWT令牌: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT令牌已过期: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("不支持的JWT令牌: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT声明字符串为空: {}", e.getMessage());
        }

        return true;
    }
}