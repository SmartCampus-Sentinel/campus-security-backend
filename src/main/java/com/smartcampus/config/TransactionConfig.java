package com.smartcampus.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 事务管理配置
 * 用于解决"SqlSession was not registered for synchronization"警告
 */
@Configuration
@EnableTransactionManagement
public class TransactionConfig {

    /**
     * 配置事务管理器
     * Spring会使用该管理器来管理MyBatis的数据库事务
     */
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
