package com.smartcampus.service.impl;

import com.smartcampus.entity.Role;
import com.smartcampus.mapper.RoleMapper;
import com.smartcampus.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色权限表 服务实现类
 * </p>
 *
 * @author SmartCampusTeam
 * @since 2026-01-20
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

}
