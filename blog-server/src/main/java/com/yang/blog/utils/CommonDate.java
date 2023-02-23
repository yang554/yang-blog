package com.yang.blog.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class CommonDate {
    /**
     * 获取当前时间戳
     * @return
     */
    public static Long getStamp(){
        Timestamp tamp = new Timestamp(System.currentTimeMillis());
        return tamp.getTime();
    }

    /**
     * 获取当前时间
     * @return
     */
    public static LocalDateTime getTime24(){
        //获取当前时间
        LocalDateTime d0 = LocalDateTime.now();
        System.out.println(d0);
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = dateFormat.format(date);
        return d0;
    }

    //获取当前时间 LocalDateTime d0 = LocalDateTime.now();
    // System.out.println(DataConvertUtil.localDateTimeToStr(d0, "yyyy-MM-dd HH:mm:ss"));
    // 输入年月日时分秒实例化新的时间对象
    // LocalDateTime d1 = LocalDateTime.of(2017, 5, 15, 20, 32, 12);
    // System.out.println(DataConvertU
}
