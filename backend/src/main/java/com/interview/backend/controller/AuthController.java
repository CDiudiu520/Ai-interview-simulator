package com.interview.backend.controller;

import com.interview.backend.entity.User;
import com.interview.backend.service.UserService;
import com.interview.backend.util.JwtUtil;
import com.interview.backend.util.TokenStore;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final TokenStore tokenStore;

    public AuthController(UserService userService, JwtUtil jwtUtil, TokenStore tokenStore) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.tokenStore = tokenStore;
    }

    // 注册
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        // 1. 存到 MySQL
        userService.create(user);

        // 2. 生成 JWT
        String token = jwtUtil.generateToken(user.getUsername());

        // 3. 存到 TokenStore（Redis 替身）
        tokenStore.put(user.getUsername(), token);

        return Map.of("token", token, "username", user.getUsername());
    }

    // 登录
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest req) {
        // 1. 从 MySQL 查用户
        User user = userService.listAll().stream()
            .filter(u -> u.getUsername().equals(req.getUsername()))
            .filter(u -> u.getPassword().equals(req.getPassword()))
            .findFirst()
            .orElse(null);

        if (user == null) {
            return Map.of("error", "用户名或密码错误");
        }

        // 2. 生成 JWT
        String token = jwtUtil.generateToken(user.getUsername());

        // 3. 存到 TokenStore
        tokenStore.put(user.getUsername(), token);

        return Map.of("token", token, "username", user.getUsername());
    }

    // 登出
    @PostMapping("/logout")
    public Map<String, String> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        if (jwtUtil.validateToken(token)) {
            String username = jwtUtil.getUsernameFromToken(token);
            tokenStore.remove(username);
            return Map.of("message", "已登出");
        }
        return Map.of("error", "Token 无效");
    }
}

// 登录请求体
class LoginRequest {
    private String username;
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
