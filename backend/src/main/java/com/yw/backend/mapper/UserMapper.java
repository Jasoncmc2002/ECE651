package com.yw.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yw.backend.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
