package com.interview.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// 健康检查接口：给 Docker healthcheck 用，GET /ping 永远返回 200
@RestController
public class HealthController {

    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of("status", "ok");
    }
}
