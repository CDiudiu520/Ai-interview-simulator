package com.interview.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.interview.backend.entity.User;
import com.interview.backend.mapper.UserMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public List<User> listAll() {
        return userMapper.selectList(null);
    }

    public User getById(Integer id) {
        return userMapper.selectById(id);
    }

    public User findByUsername(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return userMapper.selectOne(wrapper);
    }

    public boolean existsByUsername(String username) {
        return findByUsername(username) != null;
    }

    public User create(User user) {
        userMapper.insert(user);
        return user;
    }

    public User update(User user) {
        userMapper.updateById(user);
        return getById(user.getId());
    }

    public void delete(Integer id) {
        userMapper.deleteById(id);
    }
}
