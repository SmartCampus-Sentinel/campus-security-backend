package com.smartcampus.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartcampus.common.Result;
import com.smartcampus.dto.LoginDto;
import com.smartcampus.entity.User;
import com.smartcampus.service.IUserService;

@RestController
@RequestMapping("/auth") // 核心：这里定义了所有接口以 /auth 开头
public class AuthController {

    @Autowired
    private IUserService userService;

    // 登录接口：前端访问 /auth/login
    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody LoginDto loginDto) {
        // 1. 调用 Service 进行登录逻辑检查 (查数据库)
        // 注意：loginDto.getUsername() 拿到的就是前端传来的 "admin"
        User user = userService.login(loginDto.getUsername(), loginDto.getPassword());

        // 2. 生成一个令牌 (Token)
        // 在实际开发中这里会用 JWT，现在为了跑通流程，我们先生成一个随机字符串
        String token = UUID.randomUUID().toString();

        // 3. 准备返回给前端的数据 (data)
        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", String.valueOf(user.getId())); // 返回用户ID
        data.put("username", user.getUsername());

        // 4. 返回标准格式 (code:200, msg:"操作成功", data: {...})
        return Result.success(data);
    }
}