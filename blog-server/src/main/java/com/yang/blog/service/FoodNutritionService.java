package com.yang.blog.service;

import com.yang.blog.entity.FoodNutritionEntity;
import com.yang.blog.utils.RespBean;

import java.util.HashMap;
import java.util.List;

public interface FoodNutritionService {
    //查询所有记录
    List<FoodNutritionEntity> findAll();
    //条件查询
    List<FoodNutritionEntity> findByFactor(String param);
    //操作
    RespBean operationFood(HashMap<String,Object> entity);
    //删除食物
    RespBean delFood(String id);
}
