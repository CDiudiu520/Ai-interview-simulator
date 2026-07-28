package com.interview.backend.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenStore {

    // 本地开发用 ConcurrentHashMap，Docker 环境用 Redis
    private final ConcurrentHashMap<String, String> store = new ConcurrentHashMap<>();

    public void put(String username, String token) {
        store.put(username, token);
    }

    public boolean exists(String username) {
        return store.containsKey(username);
    }

    public void remove(String username) {
        store.remove(username);
    }
}
