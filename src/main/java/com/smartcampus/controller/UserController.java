package com.smartcampus.controller;

import com.smartcampus.common.Result;
import com.smartcampus.entity.User;
import com.smartcampus.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @GetMapping("/list")
    public Result<List<User>> list() {
        return Result.success(userService.list());
    }
}