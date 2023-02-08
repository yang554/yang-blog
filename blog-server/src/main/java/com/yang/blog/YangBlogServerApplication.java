package com.yang.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class YangBlogServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(YangBlogServerApplication.class, args);
    }

}
