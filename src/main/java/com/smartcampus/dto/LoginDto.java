package com.smartcampus.dto;

import lombok.Data;

@Data
public class LoginDto {
    private String username;
    private String password;
    private Integer type; // 前端传了个 type: 1，我们也得接住，虽然暂时可能不用
}