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
        // 1. 收到前端发来的 JSON → Map
        // 2. 设置请求头，告诉 Python 这是 JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        // 3. 转发给 Python
        ResponseEntity<Map> response = restTemplate.postForEntity(
            aiServiceUrl + "/chat",   // http://localhost:8000/chat
            request,                   // 请求体
            Map.class                  // 返回类型
        );

        // 4. 把 Python 的返回原样返回给前端
        return response.getBody();
    }
}
