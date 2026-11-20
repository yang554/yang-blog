package com.yang.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Spring Boot应用程序的主启动类
 * 该类使用多个注解来配置和启用Spring Boot的各种功能
 */
@SpringBootApplication // 标识这是一个Spring Boot应用程序的主启动类
@ComponentScan("com.yang.blog") // 添加扫描路径，指定Spring组件扫描的基础包
@EnableCaching // 启用Spring的缓存功能
@EnableTransactionManagement // 启用Spring的注解驱动的事务管理功能
@EnableScheduling // 启用定时任务
public class YangBlogServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(YangBlogServerApplication.class, args);
    }

}
