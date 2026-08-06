package com.interview.backend.config;

import com.interview.backend.util.JwtUtil;
import com.interview.backend.util.TokenStore;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
public class JwtFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

    private final JwtUtil jwtUtil;
    private final TokenStore tokenStore;

    public JwtFilter(JwtUtil jwtUtil, TokenStore tokenStore) {
        this.jwtUtil = jwtUtil;
        this.tokenStore = tokenStore;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String path = req.getRequestURI();

        // 白名单：不需要 Token 的路径
        if (path.startsWith("/auth/") || path.equals("/ping")
                || path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs")) {
            chain.doFilter(request, response);
            return;
        }

        // 处理 CORS 预检请求
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        // 提取 Token
        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            writeUnauthorized(res, "未登录，请先登录");
            return;
        }

        String token = authHeader.replace("Bearer ", "");

        // 验三道：签名 → 过期 → TokenStore
        if (!jwtUtil.validateToken(token)) {
            log.warn("Token 验证失败: {}", token.substring(0, Math.min(20, token.length())));
            writeUnauthorized(res, "Token 无效或已过期，请重新登录");
            return;
        }

        String username = jwtUtil.getUsernameFromToken(token);
        if (!tokenStore.exists(username)) {
            log.warn("Token 不在 TokenStore 中: {}", username);
            writeUnauthorized(res, "Token 已失效，请重新登录");
            return;
        }

        // 验证通过，把用户名放进 request，后续接口可以用
        req.setAttribute("username", username);
        chain.doFilter(request, response);
    }

    // 统一写 401 响应，必须带上 CORS 头，否则浏览器会把跨域 401 误判成网络错误
    private void writeUnauthorized(HttpServletResponse res, String message) throws IOException {
        res.setStatus(401);
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}
