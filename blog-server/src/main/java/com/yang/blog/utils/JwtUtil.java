package com.yang.blog.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret:mySecretKey}")
    private String secret;

    @Value("${jwt.expiration:1800000}") // 短期token，默认30分钟
    private Long accessTokenExpiration;

    @Value("${jwt.expiration.refresh:2592000000}") // 长期 Token：30天
    private Long refreshTokenExpiration;

    @Value("${jwt.expiration.remember-me:7776000000}") // 记住我 Token：90天
    private Long rememberMeExpiration;

    /**
     * 生成访问Token（短期）
     */
    public String generateAccessToken(String username) {
        return generateToken(username, accessTokenExpiration, "ACCESS");
    }

    /**
     * 生成刷新Token（长期）
     */
    public String generateRefreshToken(String username) {
        return generateToken(username, refreshTokenExpiration, "REFRESH");
    }

    /**
     * 生成记住我Token（超长期）
     */
    public String generateRememberMeToken(String username) {
        return generateToken(username, rememberMeExpiration, "REMEMBER_ME");
    }

    /**
     * 通用Token生成方法
     */
    private String generateToken(String username, Long expiration, String type) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("type", type);
        claims.put("created", new Date());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 获取Token类型
     */
    public String getTokenType(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.get("type", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从Token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 验证Token是否有效
     */
    public boolean validateToken(String token, String username) {
        String tokenUsername = getUsernameFromToken(token);
        return (tokenUsername.equals(username) && !isTokenExpired(token));
    }

    /**
     * 检查Token是否过期
     */
    public boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 获取Token过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }

    /**
     * 从Token中获取Claims
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 刷新Token（基于旧Token生成新Token，保持相同类型）
     */
    public String refreshToken(String oldToken) {
        String username = getUsernameFromToken(oldToken);
        String type = getTokenType(oldToken);

        if (username == null || type == null) {
            return null;
        }

        Long expiration;
        switch (type) {
            case "ACCESS":
                expiration = accessTokenExpiration;
                break;
            case "REFRESH":
                expiration = refreshTokenExpiration;
                break;
            case "REMEMBER_ME":
                expiration = rememberMeExpiration;
                break;
            default:
                expiration = accessTokenExpiration;
        }

        return generateToken(username, expiration, type);
    }
}