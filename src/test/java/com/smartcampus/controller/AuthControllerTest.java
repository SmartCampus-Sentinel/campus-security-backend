package com.smartcampus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcampus.dto.LoginDto;
import com.smartcampus.entity.Role;
import com.smartcampus.entity.User;
import com.smartcampus.mapper.RoleMapper;
import com.smartcampus.service.IUserService;
import com.smartcampus.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private IUserService userService;
    @Mock
    private RoleMapper roleMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void login_success() throws Exception {
        User user = new User();
        user.setUserId(1L);
        user.setUsername("admin");
        user.setPassword("$2a$10$hashed");
        user.setRoleId(1L);
        user.setStatus(true);

        Role role = new Role();
        role.setRoleId(1L);
        role.setRoleName("超级管理员");

        when(userService.getByUsername("admin")).thenReturn(user);
        when(passwordEncoder.matches("123456", "$2a$10$hashed")).thenReturn(true);
        when(roleMapper.selectById(1L)).thenReturn(role);
        when(jwtUtils.generateToken("admin", "超级管理员")).thenReturn("test-jwt-token");

        LoginDto dto = new LoginDto();
        dto.setUsername("admin");
        dto.setPassword("123456");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value("test-jwt-token"))
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andExpect(jsonPath("$.data.roleName").value("超级管理员"));
    }

    @Test
    void login_user_not_found() throws Exception {
        when(userService.getByUsername("unknown")).thenReturn(null);

        LoginDto dto = new LoginDto();
        dto.setUsername("unknown");
        dto.setPassword("123456");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("用户名或密码错误"));
    }

    @Test
    void login_wrong_password() throws Exception {
        User user = new User();
        user.setUsername("admin");
        user.setPassword("$2a$10$hashed");
        user.setStatus(true);

        when(userService.getByUsername("admin")).thenReturn(user);
        when(passwordEncoder.matches("wrong", "$2a$10$hashed")).thenReturn(false);

        LoginDto dto = new LoginDto();
        dto.setUsername("admin");
        dto.setPassword("wrong");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("用户名或密码错误"));
    }

    @Test
    void login_disabled_user() throws Exception {
        User user = new User();
        user.setUsername("admin");
        user.setPassword("$2a$10$hashed");
        user.setStatus(false);

        when(userService.getByUsername("admin")).thenReturn(user);
        when(passwordEncoder.matches("123456", "$2a$10$hashed")).thenReturn(true);

        LoginDto dto = new LoginDto();
        dto.setUsername("admin");
        dto.setPassword("123456");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("账号已被禁用"));
    }
}
