package com.gazi.gazi_renew.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisUtilService {
    private final RedisTemplate<String, String> redisTemplate;

    public String getData(String key) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    public void setDataExpire(String key, String value, long duration) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        Duration expireDuration = Duration.ofSeconds(duration);
        valueOperations.set(key, value, expireDuration);
    }

    public void setRefreshToken(String username, String refreshToken, long expirationTime) {
        String key = "RT:" + username;
        redisTemplate.opsForValue().set(key, refreshToken, expirationTime, TimeUnit.MILLISECONDS);
    }

    public void deleteToken(String key) {
        redisTemplate.delete(key);
    }

    public void addToBlacklist(String token, long expiration) {
        redisTemplate.opsForValue().set(token, "logout", expiration, TimeUnit.MILLISECONDS);
    }
}
