package com.smartcampus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartcampus.entity.User;
import com.smartcampus.mapper.UserMapper;
import com.smartcampus.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final PasswordEncoder passwordEncoder;

    private static final String CACHE_PREFIX = "user:";

    @Override
    public User getByUsername(String username) {
        String key = CACHE_PREFIX + username;
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached instanceof User) {
            return (User) cached;
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = this.getOne(wrapper);

        if (user != null) {
            redisTemplate.opsForValue().set(key, user, 30, TimeUnit.MINUTES);
        }

        return user;
    }

    @Override
    public void register(String username, String password, String phone) {
        if (getByUsername(username) != null) {
            throw new RuntimeException("用户名「" + username + "」已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhone(phone);
        user.setRoleId(3L);
        user.setStatus(true);
        user.setCreateTime(LocalDateTime.now());
        this.save(user);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = this.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        this.updateById(user);
        clearUserCache(user.getUsername());
    }

    public void clearUserCache(String username) {
        redisTemplate.delete(CACHE_PREFIX + username);
    }
}