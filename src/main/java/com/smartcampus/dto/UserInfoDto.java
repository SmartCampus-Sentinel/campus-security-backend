package com.smartcampus.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("当前用户信息")
public class UserInfoDto {

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("角色名称")
    private String roleName;

    @ApiModelProperty("权限标识(逗号分隔)")
    private String permissions;
}
