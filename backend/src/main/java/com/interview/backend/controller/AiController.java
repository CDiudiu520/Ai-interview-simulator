package com.interview.backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Value("${ai.service-url}")               // 从 application.yml 读取地址
    private String aiServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();  // Java版的requests

    @PostMapping("/chat")
    public Map chat(@RequestBody Map<String, Object> body) {
        return forward("/chat", body);
    }

    @PostMapping("/generate-questions")
    public Map generateQuestions(@RequestBody Map<String, Object> body) {
        return forward("/generate-questions", body);
    }

    @PostMapping("/evaluate")
    public Map evaluate(@RequestBody Map<String, Object> body) {
        return forward("/evaluate", body);
    }

    // 统一转发：收到前端请求 → 转发给 Python → 原样返回
    private Map forward(String path, Map<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(
            aiServiceUrl + path,
            request,
            Map.class
        );
        return response.getBody();
    }
}
