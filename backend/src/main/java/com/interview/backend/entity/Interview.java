package com.interview.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("interviews")
public class Interview {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String company;
    private String position;
    private String type;
    private BigDecimal score;
    private String feedback;
    private String highlights;
    private String weaknesses;
    private String suggestions;
    private LocalDateTime createdAt;

    // ===== getter 和 setter =====

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public String getHighlights() { return highlights; }
    public void setHighlights(String highlights) { this.highlights = highlights; }

    public String getWeaknesses() { return weaknesses; }
    public void setWeaknesses(String weaknesses) { this.weaknesses = weaknesses; }

    public String getSuggestions() { return suggestions; }
    public void setSuggestions(String suggestions) { this.suggestions = suggestions; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
