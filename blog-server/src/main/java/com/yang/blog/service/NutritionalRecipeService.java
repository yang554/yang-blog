package com.yang.blog.service;

import com.yang.blog.entity.NutritionalRecipeEntity;
import com.yang.blog.utils.RespBean;

import java.util.HashMap;
import java.util.List;

public interface NutritionalRecipeService {
    //查询所有记录
    List<NutritionalRecipeEntity> findAll();
    //条件查询
    List<NutritionalRecipeEntity> findByFactor(String name,String type);
    //根据ID查询
    RespBean findById(String id);
    //添加菜谱
    RespBean operationRecipe(HashMap<String,Object> hashMap);
    //删除菜谱
    RespBean delRecipe(String id);
}
