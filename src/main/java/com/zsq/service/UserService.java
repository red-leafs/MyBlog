package com.zsq.service;

import com.zsq.entity.User;

public interface UserService{

    //登录验证方法
    User login(String username, String password);

    User getUserById(Integer id);

    boolean updatePassword(Integer id, String originalPassword, String newPassword);

    boolean updateName(Integer id, String loginUserName, String nickName);
}
