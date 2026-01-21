package com.smartcampus.dto;

public class LoginDto {
    private String username;
    private String password;
    private Integer type; // 前端传了个 type: 1，我们也得接住，虽然暂时可能不用

    // 无参构造函数
    public LoginDto() {
    }

    // 有参构造函数
    public LoginDto(String username, String password, Integer type) {
        this.username = username;
        this.password = password;
        this.type = type;
    }

    // Getter 和 Setter 方法
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}