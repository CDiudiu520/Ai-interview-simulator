package com.interview.backend.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenStore {

    // key: 用户名, value: JWT Token
    // ConcurrentHashMap = 线程安全的 HashMap，Redis 的简化版替身
    private final ConcurrentHashMap<String, String> store = new ConcurrentHashMap<>();

    // 存 Token
    public void put(String username, String token) {
        store.put(username, token);
    }

    // 查 Token 是否存在且匹配
    public boolean exists(String username) {
        return store.containsKey(username);
    }

    // 删除 Token（登出）
    public void remove(String username) {
        store.remove(username);
    }
}
