package com.yang.blog.config.shiro;

import org.apache.shiro.util.SimpleByteSource;

import java.io.Serializable;

public class MyByteSource extends SimpleByteSource implements Serializable {
    public MyByteSource(String string) {
        //System.out.println("===============这里是盐===========");
        super(string);
    }
}
