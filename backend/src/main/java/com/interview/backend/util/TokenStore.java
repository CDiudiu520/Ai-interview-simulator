package com.interview.backend.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class TokenStore {

    // RedisTemplate = Spring 给的 Redis 遥控器，用法和 HashMap 几乎一样
    private final RedisTemplate<String, String> redisTemplate;

    public TokenStore(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 存 Token（设过期时间 30 分钟）
    public void put(String username, String token) {
        redisTemplate.opsForValue().set(username, token, 30, TimeUnit.MINUTES);
    }

    // 查 Token 是否存在
    public boolean exists(String username) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(username));
    }

    // 删除 Token（登出）
    public void remove(String username) {
        redisTemplate.delete(username);
    }
}
