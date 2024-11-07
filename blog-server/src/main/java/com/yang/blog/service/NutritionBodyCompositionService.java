package com.yang.blog.service;

import com.yang.blog.entity.NutritionBodyCompositionEntity;
import com.yang.blog.utils.RespBean;

import java.util.HashMap;
import java.util.List;

public interface NutritionBodyCompositionService {
    //查询所有记录
    List<NutritionBodyCompositionEntity> findAll();
    //根据ID查询
    RespBean findById(String id);
    //根据用户查询
    List<NutritionBodyCompositionEntity> findByUser(String uID);
    //添加或者修改体成分记录
    RespBean operationNBC(HashMap<String,Object> userEntity);
    //删除体成分记录
    RespBean delNBC(String id);
}
