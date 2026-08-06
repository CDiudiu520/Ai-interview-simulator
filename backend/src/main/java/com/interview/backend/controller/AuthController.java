package com.interview.backend.controller;

import com.interview.backend.entity.User;
import com.interview.backend.service.UserService;
import com.interview.backend.util.JwtUtil;
import com.interview.backend.util.TokenStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final TokenStore tokenStore;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(UserService userService, JwtUtil jwtUtil, TokenStore tokenStore,
                          BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.tokenStore = tokenStore;
        this.passwordEncoder = passwordEncoder;
    }

    // 注册
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest req) {
        // --- 输入校验 ---
        if (req.getUsername() == null || req.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "用户名不能为空"));
        }
        if (req.getPassword() == null || req.getPassword().length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("error", "密码不能少于 6 位"));
        }
        if (req.getEmail() != null && !req.getEmail().isEmpty()
                && !EMAIL_PATTERN.matcher(req.getEmail()).matches()) {
            return ResponseEntity.badRequest().body(Map.of("error", "邮箱格式不正确"));
        }
        if (userService.existsByUsername(req.getUsername().trim())) {
            return ResponseEntity.badRequest().body(Map.of("error", "用户名已存在"));
        }

        // 1. 构建 User 并加密密码
        User user = new User();
        user.setUsername(req.getUsername().trim());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setEmail(req.getEmail() != null ? req.getEmail().trim() : null);
        userService.create(user);
        log.info("新用户注册: {}", user.getUsername());

        // 2. 生成 JWT
        String token = jwtUtil.generateToken(user.getUsername());

        // 3. 存到 TokenStore
        tokenStore.put(user.getUsername(), token);

        return ResponseEntity.ok(Map.of("token", token, "username", user.getUsername()));
    }

    // 登录
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest req) {
        // --- 输入校验 ---
        if (req.getUsername() == null || req.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "用户名不能为空"));
        }
        if (req.getPassword() == null || req.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "密码不能为空"));
        }

        // 1. 按用户名查用户
        User user = userService.findByUsername(req.getUsername().trim());

        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            log.warn("登录失败: {}", req.getUsername());
            return ResponseEntity.status(401).body(Map.of("error", "用户名或密码错误"));
        }

        log.info("用户登录: {}", user.getUsername());

        // 2. 生成 JWT
        String token = jwtUtil.generateToken(user.getUsername());

        // 3. 存到 TokenStore
        tokenStore.put(user.getUsername(), token);

        return ResponseEntity.ok(Map.of("token", token, "username", user.getUsername()));
    }

    // 登出
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        if (jwtUtil.validateToken(token)) {
            String username = jwtUtil.getUsernameFromToken(token);
            tokenStore.remove(username);
            log.info("用户登出: {}", username);
            return ResponseEntity.ok(Map.of("message", "已登出"));
        }
        return ResponseEntity.status(401).body(Map.of("error", "Token 无效"));
    }
}

// 注册请求体
class RegisterRequest {
    private String username;
    private String password;
    private String email;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
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
