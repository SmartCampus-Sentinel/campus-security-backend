package com.smartcampus;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// 【关键配置】扫描 Mapper 接口，确保 MyBatis 能工作
@MapperScan("com.smartcampus.mapper")
public class SmartCampusApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartCampusApplication.class, args);
        System.out.println("----------------------------------------------------------");
        System.out.println("    校园智能安防平台后端启动成功！(Campus Security Backend)    ");
        System.out.println("----------------------------------------------------------");
    }
}