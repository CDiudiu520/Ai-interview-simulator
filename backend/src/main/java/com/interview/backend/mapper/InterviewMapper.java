package com.interview.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.interview.backend.entity.Interview;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InterviewMapper extends BaseMapper<Interview> {
    // 不用写任何方法！BaseMapper 自带：
    //   insert(interview)       新增
    //   deleteById(id)           按ID删
    //   updateById(interview)    按ID改
    //   selectById(id)           按ID查
    //   selectList(wrapper)      条件查
}
