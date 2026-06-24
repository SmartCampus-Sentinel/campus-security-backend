package com.smartcampus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcampus.common.Result;
import com.smartcampus.entity.Role;
import com.smartcampus.entity.User;
import com.smartcampus.service.IRoleService;
import com.smartcampus.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {

    private final IRoleService roleService;
    private final IUserService userService;

    @GetMapping("/list")
    public Result<Page<Role>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String roleName) {
        Page<Role> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(roleName)) {
            wrapper.like(Role::getRoleName, roleName);
        }
        wrapper.orderByDesc(Role::getCreateTime);
        return Result.success(roleService.page(page, wrapper));
    }

    @GetMapping("/{roleId}")
    public Result<Role> getById(@PathVariable Long roleId) {
        Role role = roleService.getById(roleId);
        if (role == null) {
            return Result.error("角色不存在");
        }
        return Result.success(role);
    }

    @PostMapping
    public Result<Void> add(@RequestBody Role role) {
        roleService.checkRoleNameUnique(role.getRoleName(), null);
        role.setCreateTime(java.time.LocalDateTime.now());
        roleService.save(role);
        return Result.success(null, "新增成功");
    }

    @PutMapping
    public Result<Void> update(@RequestBody Role role) {
        if (role.getRoleId() == null) {
            return Result.error("角色ID不能为空");
        }
        roleService.checkRoleNameUnique(role.getRoleName(), role.getRoleId());
        roleService.updateById(role);
        return Result.success(null, "修改成功");
    }

    @DeleteMapping("/{roleId}")
    public Result<Void> delete(@PathVariable Long roleId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getRoleId, roleId);
        if (userService.count(wrapper) > 0) {
            return Result.error("该角色下存在用户，无法删除");
        }
        roleService.removeById(roleId);
        return Result.success(null, "删除成功");
    }
}
