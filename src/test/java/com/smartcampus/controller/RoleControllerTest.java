package com.smartcampus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcampus.entity.Role;
import com.smartcampus.entity.User;
import com.smartcampus.service.IRoleService;
import com.smartcampus.service.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RoleControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private IRoleService roleService;
    @Mock
    private IUserService userService;

    @InjectMocks
    private RoleController roleController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(roleController).build();
    }

    @Test
    void add_success() throws Exception {
        when(roleService.save(any(Role.class))).thenReturn(true);

        Role role = new Role();
        role.setRoleName("测试角色");

        mockMvc.perform(post("/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(role)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("新增成功"));
    }

    @Test
    void add_duplicate_name() throws Exception {
        doThrow(new RuntimeException("角色名「测试角色」已存在"))
                .when(roleService).checkRoleNameUnique("测试角色", null);

        Role role = new Role();
        role.setRoleName("测试角色");

        try {
            mockMvc.perform(post("/role")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(role)));
        } catch (Exception e) {
            assertTrue(e.getCause().getMessage().contains("角色名「测试角色」已存在"));
        }
    }

    @Test
    void delete_has_users() throws Exception {
        when(userService.count(any(LambdaQueryWrapper.class))).thenReturn(3L);

        mockMvc.perform(delete("/role/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("该角色下存在用户，无法删除"));

        verify(roleService, never()).removeById(any());
    }

    @Test
    void delete_success() throws Exception {
        when(userService.count(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(roleService.removeById(1L)).thenReturn(true);

        mockMvc.perform(delete("/role/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("删除成功"));

        verify(roleService).removeById(1L);
    }

    @Test
    void get_by_id_found() throws Exception {
        Role role = new Role();
        role.setRoleId(1L);
        role.setRoleName("管理员");
        when(roleService.getById(1L)).thenReturn(role);

        mockMvc.perform(get("/role/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.roleName").value("管理员"));
    }

    @Test
    void get_by_id_not_found() throws Exception {
        when(roleService.getById(99L)).thenReturn(null);

        mockMvc.perform(get("/role/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("角色不存在"));
    }

    @Test
    void update_no_id() throws Exception {
        Role role = new Role();
        role.setRoleName("测试");

        mockMvc.perform(put("/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(role)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("角色ID不能为空"));
    }
}
