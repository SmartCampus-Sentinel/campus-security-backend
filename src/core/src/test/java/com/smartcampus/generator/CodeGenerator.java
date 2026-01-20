package com.smartcampus.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;

import java.util.Collections;

public class CodeGenerator {

    public static void main(String[] args) {
        // 1. 数据库配置
        String url = "jdbc:mysql://localhost:3306/campus_security?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
        String username = "root";
        String password = "Htq*20080119";

        // 2. 开始生成
        FastAutoGenerator.create(url, username, password)
                .globalConfig(builder -> {
                    builder.author("SmartCampusTeam") // 作者名称
                            .enableSwagger() // 开启 Swagger 模式
                            .outputDir(System.getProperty("user.dir") + "/src/main/java"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.smartcampus") // 父包名
                            // .moduleName("system") // 如果需要分模块（如 system），可以打开此注释
                            .pathInfo(Collections.singletonMap(OutputFile.xml, System.getProperty("user.dir") + "/src/main/resources/mapper")); // Mapper XML 输出路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("sys_user", "sys_role", "device_info", "alarm_event", "alarm_disposal", "student_report", "sensor_data") // 需要生成的表名
                            .addTablePrefix("sys_"); // 设置过滤表前缀，例如 sys_user 生成 User 类，而不是 SysUser 类

                    // Entity 策略配置
                    builder.entityBuilder()
                            .enableLombok() // 开启 Lombok
                            .enableTableFieldAnnotation(); // 开启字段注解

                    // Controller 策略配置
                    builder.controllerBuilder()
                            .enableRestStyle(); // 开启 @RestController
                })
                .templateEngine(new VelocityTemplateEngine()) // 使用 Velocity 引擎
                .execute();
    }
}