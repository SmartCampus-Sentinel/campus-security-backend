package com.smartcampus.service;

import com.smartcampus.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 系统用户表 服务类
 * </p>
 */
public interface IUserService extends IService<User> {

    // ✅ 必须加上这行定义，Controller 才能调用！
    User login(String username, String password);
}
