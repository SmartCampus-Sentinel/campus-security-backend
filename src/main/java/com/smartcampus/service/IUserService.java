package com.smartcampus.service;

import com.smartcampus.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IUserService extends IService<User> {

    User getByUsername(String username);

    void register(String username, String password, String phone);

    void changePassword(Long userId, String oldPassword, String newPassword);
}
