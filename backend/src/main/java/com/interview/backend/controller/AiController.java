package com.interview.backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.interview.backend.entity.Interview;
import com.interview.backend.mapper.InterviewMapper;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Value("${ai.service-url}")               // 从 application.yml 读取地址
    private String aiServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();  // Java版的requests
    private final InterviewMapper interviewMapper;

    public AiController(InterviewMapper interviewMapper) {
        this.interviewMapper = interviewMapper;
    }

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
        // 1. 取出 interview_id（让前端知道更新哪条记录）
        Integer interviewId = (Integer) body.get("interview_id");

        // 2. 转发 Python 拿评分结果
        Map result = forward("/evaluate", body);

        // 3. 评分成功 → 写回 DB
        if (interviewId != null && !result.containsKey("error")) {
            Interview interview = interviewMapper.selectById(interviewId);
            if (interview != null) {
                // Python 返回的 score 可能是整数，转成 BigDecimal
                Object scoreObj = result.get("score");
                if (scoreObj != null) {
                    interview.setScore(BigDecimal.valueOf(((Number) scoreObj).doubleValue()));
                }
                interview.setFeedback((String) result.get("feedback"));
                interviewMapper.updateById(interview);
            }
        }

        return result;
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
