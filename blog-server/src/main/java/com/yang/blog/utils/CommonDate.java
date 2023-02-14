package com.yang.blog.utils;

import java.sql.Timestamp;

public class CommonDate {
    public static Long getStamp(){
        Timestamp tamp = new Timestamp(System.currentTimeMillis());

        return tamp.getTime();
    }
}
