package com.zsq.mapper;

import com.zsq.entity.User;

public interface UserMapper {
    int deleteByPrimaryKey(Integer adminUserId);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer adminUserId);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User login(String username,String password);

}