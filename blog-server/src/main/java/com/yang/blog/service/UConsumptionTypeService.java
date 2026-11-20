package com.yang.blog.service;

import com.yang.blog.entity.UConsumptionTypeEntity;
import com.yang.blog.utils.RespBean;

import java.util.HashMap;
import java.util.List;

public interface UConsumptionTypeService {
    RespBean getConsumptionTypeAll();
    RespBean getConsumptionTypeByName(String name);
    //添加标签
    RespBean addConsumptionType(HashMap<String,String> hashMap);
    //根据编码修改标签
    RespBean updConsumptionTypeById(UConsumptionTypeEntity entity);
    //根据用户输入关键词匹配类型
    List<String> matchCategory(String purpose);
    //根据编码删除标签
    RespBean delConsumptionType(String id);
}
