package com.interview.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("users")          // 告诉 MyBatis-Plus：这个类对应 users 表
public class User {

    @TableId(type = IdType.AUTO)   // 主键，自增（MySQL那边配了 AUTO_INCREMENT）
    private Integer id;
    private String username;
    private String password;
    private String email;
    private LocalDateTime createdAt;   // Java 里驼峰命名，MyBatis-Plus 自动转成 created_at

    // ===== 下面都是 getter 和 setter =====

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
