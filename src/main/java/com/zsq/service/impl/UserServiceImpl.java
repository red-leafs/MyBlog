package com.zsq.service.impl;

import com.zsq.entity.User;
import com.zsq.mapper.UserMapper;
import com.zsq.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    //登录验证
    public User login(String username, String password){
        return userMapper.login(username,password);
    }

    //根据Id查询用户信息
    @Override
    public User getUserById(Integer id) {
        return userMapper.selectByPrimaryKey(id);
    }

    //更新姓名
    @Override
    public boolean updateName(Integer id, String loginUserName, String nickName) {
        User user = userMapper.selectByPrimaryKey(id);
        //当前用户非空才可以修改
        if (user != null){
            user.setLoginPassword(loginUserName);
            user.setLoginPassword(nickName);
            if (userMapper.updateByPrimaryKeySelective(user) > 0) {
                //修改成功则返回true
                return true;
            }
        }
        return false;
    }

    //更新密码
    @Override
    public boolean updatePassword(Integer id, String originalPassword, String newPassword) {
        User user = userMapper.selectByPrimaryKey(id);
        //当前用户非空才可以修改
        if (user != null){
            //比较原密码是否正确
            if (user.getLoginPassword().equals(originalPassword)){
                user.setLoginPassword(newPassword);
                if (userMapper.updateByPrimaryKeySelective(user) > 0) {
                    //修改成功则返回true
                    return true;
                }
            }
        }
        return false;
    }
}
