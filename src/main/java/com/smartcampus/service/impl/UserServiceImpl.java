package com.smartcampus.service.impl;

import com.smartcampus.entity.User;
import com.smartcampus.mapper.UserMapper;
import com.smartcampus.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统用户表 服务实现类
 * </p>
 *
 * @author SmartCampusTeam
 * @since 2026-01-20
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
