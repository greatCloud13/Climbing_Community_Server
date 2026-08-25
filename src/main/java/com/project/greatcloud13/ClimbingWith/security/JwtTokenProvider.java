package com.project.greatcloud13.ClimbingWith.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    public static final String TOKEN_TYPE_ACCESS = "access";
    public static final String TOKEN_TYPE_REFRESH = "refresh";

    private static final String CLAIM_TOKEN_TYPE = "type";

    private final SecretKey secretKey;
    private final long accessExpirationTime;
    private final long refreshExpirationTime;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-expiration}") long accessExpirationTime,
            @Value("${jwt.refresh-expiration}") long refreshExpirationTime){
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessExpirationTime = accessExpirationTime;
        this.refreshExpirationTime = refreshExpirationTime;
    }

    /**
     * Access Token 생성
     */
    public String generateAccessToken(String username){
        return generateToken(username, TOKEN_TYPE_ACCESS, accessExpirationTime);
    }

    /**
     * Refresh Token 생성
     */
    public String generateRefreshToken(String username){
        return generateToken(username, TOKEN_TYPE_REFRESH, refreshExpirationTime);
    }

    private String generateToken(String username, String tokenType, long expirationTime){
        log.info("JWT {} 토큰 생성 사용자: {}", tokenType, username);
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .subject(username)
                .claim(CLAIM_TOKEN_TYPE, tokenType)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * JWT 토큰에서 사용자명 추출
     */
    public String getUsernameFormToken(String token){
        return parseClaims(token).getSubject();
    }

    /**
     * JWT 토큰에서 토큰 종류(access/refresh) 추출
     */
    public String getTokenType(String token){
        return parseClaims(token).get(CLAIM_TOKEN_TYPE, String.class);
    }

    /**
     * 토큰 유효성 검증
     */
    public boolean validateToken(String token){
        try{
            parseClaims(token);
            return true;
        }catch (Exception e){
            log.error("JWT 토큰 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    private Claims parseClaims(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
