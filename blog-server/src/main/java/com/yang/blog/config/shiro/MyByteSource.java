package com.yang.blog.config.shiro;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.util.SimpleByteSource;

import java.io.Serializable;

@Slf4j
public class MyByteSource extends SimpleByteSource implements Serializable {
    public MyByteSource(String string) {
//        log.info("===============这里是盐===========");
        super(string);
    }
}
