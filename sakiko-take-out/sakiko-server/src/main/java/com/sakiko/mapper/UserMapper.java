package com.sakiko.mapper;

import com.sakiko.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE openid = #{id}")
    User getByOpenid(String openid);

    void insert(User user);

    @Select("SELECT * FROM user WHERE id = #{id}")
    User getById(Long userId);
}
