package com.smartcampus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartcampus.entity.Role;
import com.smartcampus.mapper.RoleMapper;
import com.smartcampus.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    @Override
    public void checkRoleNameUnique(String roleName, Long excludeRoleId) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleName, roleName);
        if (excludeRoleId != null) {
            wrapper.ne(Role::getRoleId, excludeRoleId);
        }
        if (this.count(wrapper) > 0) {
            throw new RuntimeException("角色名「" + roleName + "」已存在");
        }
    }
}
