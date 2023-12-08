package com.yang.blog.service;

import com.yang.blog.utils.RespBean;

import java.util.HashMap;

public interface UEventService {
    //分页查询
    RespBean getEventByPage(String type, String eTitle,String uName,String createName);
    //添加
    RespBean addEvent(HashMap<String,Object> entity);
}
