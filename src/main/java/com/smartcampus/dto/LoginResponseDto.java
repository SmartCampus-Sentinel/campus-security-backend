package com.smartcampus.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("登录响应")
public class LoginResponseDto {

    @ApiModelProperty("JWT Token")
    private String token;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("角色名称")
    private String roleName;
}
