package com.smartcampus.dto;

import lombok.Data;

@Data
public class PageQueryDto {

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
