package com.yang.blog.service;

import com.yang.blog.utils.RespBean;

import java.util.HashMap;
import java.util.Map;

public interface UEventService {
    //分页查询
    Map<String,Object> getEventByPage(String id, String eTitle,String uName,String createName);
    //添加
    RespBean addEvent(HashMap<String,Object> entity);
}
