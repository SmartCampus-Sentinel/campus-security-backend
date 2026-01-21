package com.smartcampus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartcampus.entity.User;
import com.smartcampus.mapper.UserMapper;
import com.smartcampus.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统用户表 服务实现类
 * </p>
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public User login(String username, String password) {
        // 1. 构建查询条件：WHERE username = ? AND password = ?
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        wrapper.eq(User::getPassword, password);

        // 2. 查数据库
        User user = this.getOne(wrapper);

        // 3. 判断结果
        if (user == null) {
            throw new RuntimeException("登录失败：用户名或密码错误！");
        }

        return user;
    }
}