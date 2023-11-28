package com.yang.blog.service;

import com.yang.blog.utils.RespBean;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.Map;

public interface UAuntService {
    //查询用户
    RespBean findNameAll();
    //根据用户查询所有数据
    Map<String,Object> selectByNameAll(String uName,String uStatus);
    //添加
    RespBean addAunt(HashMap<String,Object> entity);
    //修改
    RespBean updAunt(HashMap<String,Object> entity);
}
