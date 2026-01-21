package com.smartcampus.controller; // 确保包名正确

import com.smartcampus.entity.User;
import com.smartcampus.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    // 测试接口：获取所有用户
    // 浏览器访问：http://localhost:8080/user/list
    @GetMapping("/list")
    public List<User> list() {
        return userService.list();
    }
}