package com.interview.backend.service;

import com.interview.backend.entity.User;
import com.interview.backend.mapper.UserMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service                         // 告诉 Spring：这是一个业务层组件
public class UserService {

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {   // 构造器注入（Spring 自动把 Mapper 传进来）
        this.userMapper = userMapper;
    }

    // ===== 5 个方法，每个一行 =====

    public List<User> listAll() {           // 查全部
        return userMapper.selectList(null);
    }

    public User getById(Integer id) {       // 按ID查
        return userMapper.selectById(id);
    }

    public User create(User user) {         // 新增
        userMapper.insert(user);
        return user;                        // insert 后 id 自动回填到 user 对象
    }

    public User update(User user) {         // 修改
        userMapper.updateById(user);
        return getById(user.getId());      // 返回更新后的数据
    }

    public void delete(Integer id) {        // 删除
        userMapper.deleteById(id);
    }
}
