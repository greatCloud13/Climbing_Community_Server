package com.project.greatcloud13.ClimbingWith.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;

/**
 * Refresh Token을 Redis에 사용자당 1개씩 저장한다 (key: refresh:{username}).
 * 재발급(Rotation) 시 기존 값을 덮어쓰므로 이전에 발급된 Refresh Token은 자동으로 무효화된다.
 */
@Component
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final String KEY_PREFIX = "refresh:";

    private final StringRedisTemplate redisTemplate;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationTime;

    public void save(String username, String refreshToken){
        redisTemplate.opsForValue().set(key(username), refreshToken, Duration.ofMillis(refreshExpirationTime));
    }

    public boolean matches(String username, String refreshToken){
        String saved = redisTemplate.opsForValue().get(key(username));
        return Objects.equals(saved, refreshToken);
    }

    public void delete(String username){
        redisTemplate.delete(key(username));
    }

    private String key(String username){
        return KEY_PREFIX + username;
    }
}
