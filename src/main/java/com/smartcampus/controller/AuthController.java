package com.smartcampus.controller;

import com.smartcampus.common.Result;
import com.smartcampus.dto.*;
import com.smartcampus.entity.Role;
import com.smartcampus.entity.User;
import com.smartcampus.mapper.RoleMapper;
import com.smartcampus.security.LoginUser;
import com.smartcampus.service.IUserService;
import com.smartcampus.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IUserService userService;
    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/login")
    public Result<LoginResponseDto> login(@RequestBody LoginDto loginDto) {
        User user = userService.getByUsername(loginDto.getUsername());
        if (user == null) {
            return Result.error("用户名或密码错误");
        }

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            return Result.error("用户名或密码错误");
        }

        if (Boolean.FALSE.equals(user.getStatus())) {
            return Result.error("账号已被禁用");
        }

        String roleName = null;
        if (user.getRoleId() != null) {
            Role role = roleMapper.selectById(user.getRoleId());
            if (role != null) {
                roleName = role.getRoleName();
            }
        }

        String token = jwtUtils.generateToken(user.getUsername(), roleName);

        LoginResponseDto response = new LoginResponseDto();
        response.setToken(token);
        response.setUsername(user.getUsername());
        response.setRoleName(roleName);

        return Result.success(response, "登录成功");
    }

    @GetMapping("/info")
    public Result<UserInfoDto> info(@AuthenticationPrincipal LoginUser loginUser) {
        UserInfoDto info = new UserInfoDto();
        info.setUserId(loginUser.getUserId());
        info.setUsername(loginUser.getUsername());
        info.setRoleName(loginUser.getRoleName());
        info.setPermissions(loginUser.getPermissions());
        return Result.success(info);
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDto dto) {
        userService.register(dto.getUsername(), dto.getPassword(), dto.getPhone());
        return Result.success(null, "注册成功");
    }

    @PutMapping("/password")
    public Result<Void> changePassword(@AuthenticationPrincipal LoginUser loginUser,
                                       @Valid @RequestBody ChangePasswordDto dto) {
        userService.changePassword(loginUser.getUserId(), dto.getOldPassword(), dto.getNewPassword());
        return Result.success(null, "密码修改成功");
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            long ttl = jwtUtils.getRemainingTtl(token);
            if (ttl > 0) {
                redisTemplate.opsForValue().set("jwt:blacklist:" + token, "1", ttl, TimeUnit.MILLISECONDS);
            }
        }
        return Result.success(null, "登出成功");
    }
}