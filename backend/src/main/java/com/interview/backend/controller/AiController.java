package com.interview.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(AiController.class);

    @Value("${ai.service-url}")
    private String aiServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();
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
                interview.setHighlights(toStringArrayJson(result.get("highlights")));
                interview.setWeaknesses(toStringArrayJson(result.get("weaknesses")));
                interview.setSuggestions(toStringArrayJson(result.get("suggestions")));
                interviewMapper.updateById(interview);
            }
        }

        return result;
    }

    // 统一转发：收到前端请求 → 转发给 Python → 原样返回
    private Map forward(String path, Map<String, Object> body) {
        long start = System.currentTimeMillis();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(
            aiServiceUrl + path,
            request,
            Map.class
        );
        long elapsed = System.currentTimeMillis() - start;
        log.info("AI 调用 {} 耗时 {}ms", path, elapsed);
        return response.getBody();
    }

    // Python 返回的评分维度是 List<String>，转成 JSON 数组字符串存 DB（TEXT 列）
    private String toStringArrayJson(Object obj) {
        if (!(obj instanceof java.util.List)) return null;
        try {
            java.util.List<?> list = (java.util.List<?>) obj;
            if (list.isEmpty()) return "[]";
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(list);
        } catch (Exception e) {
            log.warn("评分维度转 JSON 失败: {}", e.getMessage());
            return "[]";
        }
    }
}
