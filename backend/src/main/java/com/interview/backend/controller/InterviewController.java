package com.interview.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.interview.backend.entity.Interview;
import com.interview.backend.entity.User;
import com.interview.backend.mapper.InterviewMapper;
import com.interview.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/interviews")
public class InterviewController {

    private final InterviewMapper interviewMapper;
    private final UserService userService;

    public InterviewController(InterviewMapper interviewMapper, UserService userService) {
        this.interviewMapper = interviewMapper;
        this.userService = userService;
    }

    // 创建面试
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, String> body,
                                                       HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "用户不存在"));
        }

        Interview interview = new Interview();
        interview.setUserId(user.getId());
        interview.setCompany(body.getOrDefault("company", ""));
        interview.setPosition(body.getOrDefault("position", ""));
        interview.setType(body.getOrDefault("type", "tech"));

        interviewMapper.insert(interview);

        return ResponseEntity.ok(Map.of(
            "id", interview.getId(),
            "company", interview.getCompany(),
            "position", interview.getPosition()
        ));
    }

    // 面试统计
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "用户不存在"));
        }

        List<Interview> interviews = interviewMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Interview>()
                .eq("user_id", user.getId())
        );

        int total = interviews.size();
        double avgScore = interviews.stream()
            .filter(i -> i.getScore() != null)
            .mapToDouble(i -> i.getScore().doubleValue())
            .average()
            .orElse(0.0);

        // 最近一次面试
        Interview latest = interviews.isEmpty() ? null : interviews.get(0);

        return ResponseEntity.ok(Map.of(
            "total", total,
            "avgScore", Math.round(avgScore * 10.0) / 10.0,
            "latestCompany", latest != null ? latest.getCompany() : "",
            "latestPosition", latest != null ? latest.getPosition() : ""
        ));
    }

    // 查询当前用户的面试列表（分页）
    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "用户不存在"));
        }

        // 先查总数
        long total = interviewMapper.selectCount(
            new QueryWrapper<Interview>().eq("user_id", user.getId())
        );

        // 手动分页：计算 offset，用 last() 限制条数
        int offset = (page - 1) * size;
        List<Interview> records = interviewMapper.selectList(
            new QueryWrapper<Interview>()
                .eq("user_id", user.getId())
                .orderByDesc("created_at")
                .last("LIMIT " + offset + ", " + size)
        );

        return ResponseEntity.ok(Map.of(
            "count", (int) total,
            "page", page,
            "size", size,
            "data", records
        ));
    }

    // 查询单条面试详情（含评分报告），校验属于当前用户
    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable int id, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "用户不存在"));
        }

        Interview interview = interviewMapper.selectById(id);
        if (interview == null) {
            return ResponseEntity.status(404).body(Map.of("error", "面试记录不存在"));
        }
        if (!interview.getUserId().equals(user.getId())) {
            return ResponseEntity.status(403).body(Map.of("error", "无权查看他人的面试记录"));
        }

        return ResponseEntity.ok(interview);
    }
}
