package com.interview.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.interview.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper                          // 告诉 Spring：这是一个数据访问接口
public interface UserMapper extends BaseMapper<User> {
    // 不用写任何方法！BaseMapper 自带：
    //   insert(user)           新增
    //   deleteById(id)         按ID删
    //   updateById(user)       按ID改
    //   selectById(id)         按ID查
    //   selectList(wrapper)    条件查（全查用 null）
}
