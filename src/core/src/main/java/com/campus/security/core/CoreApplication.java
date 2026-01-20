package com.campus.security.core;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 校园安防平台启动类（移除Lombok）
 */
@SpringBootApplication(scanBasePackages = "com.campus.security")
//@MapperScan("com.campus.security.mapper") // 扫描MyBatis-Plus Mapper接口
public class CoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);
        System.out.println("=====================================");
        System.out.println("  校园安防后端启动成功（端口：8080）");
        System.out.println("  访问地址：http://localhost:8080/api/health");
        System.out.println("=====================================");
    }
}